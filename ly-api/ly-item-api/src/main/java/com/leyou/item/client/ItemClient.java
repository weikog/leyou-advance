package com.leyou.item.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("item-service")
public interface ItemClient {

    /**
     * 查询所有Spu对象接口，如果是分页查询也可以
     * 商品的分页查询
     */
    @GetMapping("/spu/page")
    public PageResult<SpuDTO> goodsPageQuery(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                             @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                             @RequestParam(value = "key", required = false) String key,
                                             @RequestParam(value = "saleable", required = false) Boolean saleable);

    /*根据spuId查询Sku集合*/
    @GetMapping("/sku/of/spu")
    public List<Sku> findSkusBySpuId(@RequestParam("id") Long id);

    /*根据第三级分类查询规格参数列表*/
    @GetMapping("/spec/params")
    public List<SpecParam> findSpecParams(@RequestParam(value = "gid", required = false) Long gid,
                                          @RequestParam(value = "cid", required = false) Long cid,
                                          @RequestParam(value = "searching", required = false) Boolean searching);

    /*根据SpuId查询SpuDetail对象*/
    @GetMapping("/spu/detail")
    public SpuDetail findSpuDetailById(@RequestParam("id") Long id);

    /*根据分类id的集合查询分类对象的集合*/
    @GetMapping("/category/list")
    public List<Category> findCategorysByIds(@RequestParam("ids") List<Long> ids);

    /*根据品牌id查询品牌对象*/
    @GetMapping("/brand/{id}")
    public Brand findBrandById(@PathVariable("id") Long id);

    /*根据spuId查询Spu对象*/
    @GetMapping("/spu/{id}")
    public SpuDTO findSpuById(@PathVariable("id") Long id);

    /*根据第三级分类id查询规格组和组内参数*/
    @GetMapping("/spec/of/category")
    public List<SpecGroupDTO> findSpecByCid(@RequestParam("id") Long id);

    /*根据sku的id的集合查询sku对象的集合*/
    @GetMapping("/sku/list")
    public List<Sku> findSkusByIds(@RequestParam("ids") List<Long> ids);

    /*减库存*/
    @PutMapping("/stock/minus")
    public Void minusStock(@RequestBody Map<Long, Integer> paramMap);
}
