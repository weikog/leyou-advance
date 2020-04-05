package com.leyou.search.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.SearchRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchRepository searchRepository;

    /**
     * 将数据源的数据写入索引库
     */
    @Test
    public void indexWrite(){
        //定义查询的页数，每页条数，总页数
        Integer page = 1, rows = 100, totalPage = 1;
        do{
            //查询指定页的Spu数据，不带搜索条件，只查询上架商品数据
            PageResult<SpuDTO> pageResult = itemClient.goodsPageQuery(page, rows, null, true);
            //获取数据列表
            List<SpuDTO> spuDTOS = pageResult.getItems();
            //把Spu集合转成Goods集合
            List<Goods> goodsList = spuDTOS.stream().map(searchService::buildGoods).collect(Collectors.toList());
            //批量写入索引库
            searchRepository.saveAll(goodsList);
            //获取总页数
            totalPage = pageResult.getTotalPage();
            //页码往后自增
            page++;
        }while (page<=totalPage);//当当前页数不大于总页的时候执行
    }

    /**
     * 测试feign接口
     */
    @Test
    public void findSpecParams(){
        List<SpecParam> specParams = itemClient.findSpecParams(null, 76l, true);
        System.out.println(specParams);
    }
}
