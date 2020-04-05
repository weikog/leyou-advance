package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 商品的分页查询
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> goodsPageQuery(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                             @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                             @RequestParam(value = "key", required = false) String key,
                                                             @RequestParam(value = "saleable", required = false) Boolean saleable){
        PageResult<SpuDTO> pageResult = goodsService.goodsPageQuery(page, rows, key, saleable);
        return ResponseEntity.ok(pageResult);
    }

    /*商品保存*/
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*商品上下架操作*/
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam("id") Long id,
                                               @RequestParam("saleable") Boolean saleable){
        goodsService.updateSaleable(id, saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*根据spuId查询Sku集合*/
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<Sku>> findSkusBySpuId(@RequestParam("id") Long id){
        List<Sku> list = goodsService.findSkusBySpuId(id);
        return ResponseEntity.ok(list);
    }

    /*根据SpuId查询SpuDetail对象*/
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetail> findSpuDetailById(@RequestParam("id") Long id){
        SpuDetail spuDetail = goodsService.findSpuDetailById(id);
        return ResponseEntity.ok(spuDetail);
    }

    /*根据spuId查询Spu对象*/
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable("id") Long id){
        SpuDTO spuDTO = goodsService.findSpuById(id);
        return ResponseEntity.ok(spuDTO);
    }

    /*根据sku的id的集合查询sku对象的集合*/
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> findSkusByIds(@RequestParam("ids") List<Long> ids){
        List<Sku> list = goodsService.findSkusByIds(ids);
        return ResponseEntity.ok(list);
    }

    /*减库存*/
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> minusStock(@RequestBody Map<Long, Integer> paramMap){
        goodsService.minusStock(paramMap);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
