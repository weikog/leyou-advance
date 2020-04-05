package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.entity.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 品牌分页查询
     */
    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<Brand>> brandPageQuery(@RequestParam(value = "key", required = false) String key,
                                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                            @RequestParam(value = "sortBy", required = false) String sortBy,
                                                            @RequestParam(value = "desc", required = false) Boolean desc){
        PageResult<Brand> pageResult = brandService.brandPageQuery(key, page, rows, sortBy, desc);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 品牌添加
     * @RequestParam("cids") 注解可以将77,84格式的字符串，直接转成List集合
     */
    @PostMapping("/brand")
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*根据品牌id查询品牌对象*/
    @GetMapping("/brand/{id}")
    public ResponseEntity<Brand> findBrandById(@PathVariable("id") Long id){
        Brand brand = brandService.findBrandById(id);
        return ResponseEntity.ok(brand);
    }

    /*根据分类id查询品牌列表*/
    @GetMapping("/brand/of/category")
    public ResponseEntity<List<Brand>> findBrandByCid(@RequestParam("id") Long id){
        List<Brand> list = brandService.findBrandByCid(id);
        return ResponseEntity.ok(list);
    }

}
