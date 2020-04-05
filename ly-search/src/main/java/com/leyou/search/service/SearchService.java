package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.*;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.SearchRepository;
import com.leyou.search.utils.HighlightUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private ItemClient itemClient;

    /**
     * 将数据源【数据库】的数据转成索引库的文档对象
     * 其本质：是将数据库中一个Spu对象转成一个索引库中的Goods对象
     */
    public Goods buildGoods(SpuDTO spuDTO){
        //第四步：根据Spu的id查询Sku集合【封装skus，price，all字段】
        List<Sku> skus = itemClient.findSkusBySpuId(spuDTO.getId());
        //由于索引库中无需存放Sku全部信息，那么我们需要定义一个map来存放所需要的信息
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skuMap.put("price", sku.getPrice());
            skuMap.put("title", sku.getTitle().substring(spuDTO.getName().length()));//SpuName和SkuName除去SpuName之外的部分拼接
            skuList.add(skuMap);
        });
        //收集所有sku价格的set集合
        Set<Long> prices = skus.stream().map(Sku::getPrice).collect(Collectors.toSet());
        //拼接all字段
        String all = spuDTO.getCategoryName()
                +spuDTO.getBrandName()
                +skus.stream().map(Sku::getTitle).collect(Collectors.joining());

        //第五步：根据第三级分类id查询参与搜索的规格参数【Specs字段的key】
        List<SpecParam> specParams = itemClient.findSpecParams(null, spuDTO.getCid3(), true);
        //第六步：根据Spu的id查询SpuDetial对象【Specs字段的value】
        SpuDetail spuDetail = itemClient.findSpuDetailById(spuDTO.getId());
        //获取通用规格参数值所在的字段
        String genericSpecStr = spuDetail.getGenericSpec();
        //将字符串格式通用规格参数转成Map对象
        Map<Long, Object> genericSpecObj = JsonUtils.toMap(genericSpecStr, Long.class, Object.class);
        //获取特有规格参数值所在的字段
        String specialSpecStr = spuDetail.getSpecialSpec();
        //将字符串个数的特有规格参数转成Map对象
        Map<Long, List<Object>> specialSpecObj = JsonUtils.nativeRead(specialSpecStr, new TypeReference<Map<Long, List<Object>>>() {});

        //创建一个存储动态过滤条件的map
        Map<String, Object> specs = new HashMap<>();
        //specs这个Map中键值对的数量取决于key所在的specParams集合的长度
        specParams.forEach(specParam -> {
            String key = specParam.getName();//key值就是规格参数的名称
            Object value = null;//值有两个来源，genericSpecObj和specialSpecObj
            //判断当前规格参数是否为通用规格参数
            if(specParam.getGeneric()){
                //value的值来自于genericSpecObj
                value = genericSpecObj.get(specParam.getId());
            }else {
                //value的值来自于specialSpecObj
                value = specialSpecObj.get(specParam.getId());
            }
            //对所有数字类型的过滤条件进行处理，兑换成区间存入索引库
            if(specParam.getNumeric()){
                value = chooseSegment(value, specParam);
            }
            //给specs赋值
            specs.put(key, value);
        });

        //第一步：构建一个Goods对象
        Goods goods = new Goods();
        //第二步：先给可以直接通过Spu对象来赋值的属性赋值
        goods.setId(spuDTO.getId());
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setSpuName(spuDTO.getName());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        goods.setSubTitle(spuDTO.getSubTitle());
        //第三步：把剩余的属性都列出来备用
        goods.setSkus(JsonUtils.toString(skuList));
        goods.setPrice(prices);
        goods.setAll(all);
        goods.setSpecs(specs);
        return goods;
    }

    //把数字类型的过滤条件的值兑换成区间
    private String chooseSegment(Object value, SpecParam p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /*商品分页查询的业务代码*/
    public PageResult<GoodsDTO> goodsPageQuery(SearchRequest request) {
        //创建一个封装复杂条件查询的对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加要查询的字段条件
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "spuName", "subTitle", "skus"}, null));
        //添加分页条件
        nativeSearchQueryBuilder.withPageable(PageRequest.of(request.getPage()-1, request.getSize()));
        //添加查询条件
        nativeSearchQueryBuilder.withQuery(handlerQueryParams(request));
        //设置高亮字段
        HighlightUtils.highlightField(nativeSearchQueryBuilder, "spuName");
        //向索引库发起复杂查询请求
        AggregatedPage<Goods> goodsAgg = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class, HighlightUtils.highlightBody(Goods.class, "spuName"));
        //封装要返回的分页对象
        PageResult<GoodsDTO> pageResult = new PageResult<>(
                goodsAgg.getTotalElements(),
                goodsAgg.getTotalPages(),
                BeanHelper.copyWithCollection(goodsAgg.getContent(), GoodsDTO.class)
        );
        return pageResult;
    }

    /**
     * 过滤条件数据查询
     * 包含固定的分类和品牌过滤条件和动态的规格参数过滤条件
     */
    public Map<String, List<?>> queryFilterParams(SearchRequest request) {
        //初始化一个map来存储过滤条件
        Map<String, List<?>> filterParamsMap = new LinkedHashMap<>();
        //创建一个封装复杂条件查询的对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加要查询的字段条件[这一步可以不写]
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        //添加分页条件SpringDataElasticSearch中不能一条都不查，要想不查询数据，就设置查询一条就行了。[这一步可以不写]
        nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 1));
        //添加查询条件
        nativeSearchQueryBuilder.withQuery(handlerQueryParams(request));
        //定义分类聚合结果的名称
        String categoryAggName = "categoryAgg";
        //添加分类聚合条件 默认查询十个聚合结果 如果要查询全部，可以加.size(Integer.MAX_VALUE)
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("categoryId"));
        //定义品牌的聚合结果的名称
        String brandAggName = "brandAgg";
        //添加品牌集合条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //向索引库发起聚合查询请求
        AggregatedPage<Goods> goodsAgg = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
        //得到所有的聚合结果
        Aggregations aggregations = goodsAgg.getAggregations();
        //通过分类聚合名称获取到分类的聚合结果
        Terms categoryTerm = aggregations.get(categoryAggName);
        //解析分类聚合结果并把最终需要的数据放入到结果的filterParamsMap中
        List<Long> cids = handlerCategoryAgg(filterParamsMap, categoryTerm);
        //通过品牌的聚合名称得到品牌的聚合结果
        Terms brandTerm = aggregations.get(brandAggName);
        //解析品牌聚合结果并把最终需要的数据放入到结果的filterParamsMap中
        handlerBrandAgg(filterParamsMap, brandTerm);
        //封装动态规格参数过滤条件
        handlerSpecParamsFilterParams(filterParamsMap, handlerQueryParams(request), cids);
        return filterParamsMap;
    }

    //封装动态规格参数过滤条件
    private void handlerSpecParamsFilterParams(Map<String, List<?>> filterParamsMap, QueryBuilder handlerQueryParams, List<Long> cids) {
        cids.forEach(cid->{
            //创建一个封装复杂条件查询的对象
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            //添加要查询的字段条件[这一步可以不写]
            nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
            //添加分页条件SpringDataElasticSearch中不能一条都不查，要想不查询数据，就设置查询一条就行了。[这一步可以不写]
            nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 1));
            //添加查询条件
            nativeSearchQueryBuilder.withQuery(handlerQueryParams);

            //根据分类id查询出当前分类下所有可以被搜索的过滤字段
            List<SpecParam> specParams = itemClient.findSpecParams(null, cid, true);

            specParams.forEach(specParam -> {
                //定义分类聚合结果的名称
                String aggName = specParam.getName();
                //获取当前规格参数在索引库中对应的域名
                String filedName = "specs."+specParam.getName()+".keyword";
                //添加分类聚合条件 默认查询十个聚合结果 如果要查询全部，可以加.size(Integer.MAX_VALUE)
                nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(aggName).field(filedName));
            });

            //向索引库发起聚合查询请求
            AggregatedPage<Goods> goodsAgg = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
            //得到所有的聚合结果
            Aggregations aggregations = goodsAgg.getAggregations();
            //循环取出规格参数过滤条件并封装到map中
            specParams.forEach(specParam -> {
                //定义分类聚合结果的名称
                String aggName = specParam.getName();
                //通过分类聚合名称获取到分类的聚合结果
                Terms specParamTerm = aggregations.get(aggName);
                //解析聚合结果并把最终需要的数据放入到结果的filterParamsMap中
                List<String> specParamsFilterParamList = specParamTerm.getBuckets()
                        .stream()
                        .map(Terms.Bucket::getKeyAsString)
                        .collect(Collectors.toList());
                //存入filterParamsMap中
                filterParamsMap.put(aggName, specParamsFilterParamList);
            });

        });
    }

    //封装搜索页面的查询条件，条件可以是用户输入key，也可以是点击的过滤条件
    private QueryBuilder handlerQueryParams(SearchRequest request) {
        //提供一个过滤查询的Bool对象
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //封装搜索条件  .operator(Operator.AND)表示商品中要出现所有分词后的关键字才能匹配到
        queryBuilder.must(QueryBuilders.multiMatchQuery(request.getKey(), "spuName", "all").operator(Operator.AND));
        //获取到要封装的过滤条件参数
        Map<String, Object> filterParams = request.getFilterParams();
        if(!CollectionUtils.isEmpty(filterParams)){
            //遍历
            filterParams.entrySet().forEach(entry->{
                //获取key
                String key = entry.getKey();
                //对key进行处理
                if(StringUtils.equals(key, "分类")){
                    key = "categoryId";
                }else if(StringUtils.equals(key, "品牌")){
                    key = "brandId";
                }else {
                    key = "specs."+key+".keyword";
                }
                //获取value
                Object value = entry.getValue();
                //封装过滤条件
                queryBuilder.filter(QueryBuilders.termQuery(key, value));
            });
        }
        return queryBuilder;
    }

    //解析品牌聚合结果并把最终需要的数据放入到结果的filterParamsMap中
    private void handlerBrandAgg(Map<String, List<?>> filterParamsMap, Terms brandTerm) {
        List<Brand> brands = brandTerm.getBuckets()//得到品牌聚合后所有结果的桶集合信息
                .stream()//将桶集合转成流
                .map(Terms.Bucket::getKeyAsNumber)//收集桶中的key为Number类型
                .map(Number::longValue)//将Number类型的key收集成为Long类型
                .map(itemClient::findBrandById)//将Long类型的key再次收集成为Brand对象
                .collect(Collectors.toList());//把所有Brand对象收集成为List集合
        //给存放过滤条件的Map赋值
        filterParamsMap.put("品牌", brands);
    }

    //解析分类聚合结果并把最终需要的数据放入到结果的filterParamsMap中
    private List<Long> handlerCategoryAgg(Map<String, List<?>> filterParamsMap, Terms categoryTerm) {
        List<Long> cids = categoryTerm.getBuckets()
                .stream()
                .map(Terms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        //根据分类id的集合查询分类对象的集合
        List<Category> categoryList = itemClient.findCategorysByIds(cids);
        //给存放过滤条件的Map赋值
        filterParamsMap.put("分类", categoryList);
        return cids;
    }

    //添加索引库
    public void indexWrite(Long id) {
        //查询spuDTO对象
        SpuDTO spudto = itemClient.findSpuById(id);
        //将spuDTO对象转成Goods
        Goods goods = buildGoods(spudto);
        //保存索引库
        searchRepository.save(goods);
    }

    //删除索引库
    public void indexDelete(Long id) {
        searchRepository.deleteById(id);
    }
}
