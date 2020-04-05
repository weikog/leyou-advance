package com.leyou.page.service;

import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;

    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;

    /*获取静态化页面的上下文*/
    public Map<String, Object> loadSpuData(Long spuId) {
        //第二步：根据spuId查询SpuDTO对象
        SpuDTO spuDTO = itemClient.findSpuById(spuId);

        //第三步：根据三级分类的id查询三级分类的对象集合
        List<Category> categorys = itemClient.findCategorysByIds(spuDTO.getCategoryIds());

        //第四步：根据品牌id查询品牌对象
        Brand brand = itemClient.findBrandById(spuDTO.getBrandId());

        //第五步：根据第三级分类的id查询规格组集合及其下规格参数集合
        List<SpecGroupDTO> specGroupDTOS = itemClient.findSpecByCid(spuDTO.getCid3());

        //第一步：创建返回值map对象
        Map<String, Object> spuDataMap = new HashMap<>();
        spuDataMap.put("categories", categorys);
        spuDataMap.put("brand", brand);
        spuDataMap.put("spuName", spuDTO.getName());
        spuDataMap.put("subTitle", spuDTO.getSubTitle());
        spuDataMap.put("detail", spuDTO.getSpuDetail());
        spuDataMap.put("skus", spuDTO.getSkus());
        spuDataMap.put("specs", specGroupDTOS);
        //第六步：使用二三四五步得到的数据给第一步的map赋值并返回结果
        return spuDataMap;
    }

    /*生成静态化页面的业务方法*/
    public void createStaticItemPage(Long spuId){
        //准备静态页面所需要的上下文
        Context context = new Context();
        context.setVariables(loadSpuData(spuId));

        //提供静态化页面服务器的文件对象
        File itemServerFile = new File(itemDir);
        //指定静态页的名称，名称直接是spuId.html
        String itemPageName = spuId+".html";
        //准备打印流将静态化页面输出到静态页面服务器
        try(PrintWriter write = new PrintWriter(new File(itemServerFile, itemPageName))){
            //使用模板引擎生成商品的静态化页面
            templateEngine.process(itemTemplate, context, write);
        }catch (Exception e){
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }

    //删除静态页
    public void delStaticPage(Long id) {
        //提供静态化页面服务器的文件对象
        File itemServerFile = new File(itemDir);
        //指定静态页的名称，名称直接是spuId.html
        String itemPageName = id+".html";
        //得到静态页文件对象
        File pageFile = new File(itemServerFile, itemPageName);
        //如果当前静态页存在，则删除
        if(pageFile.exists()){
            pageFile.delete();
        }
    }
}
