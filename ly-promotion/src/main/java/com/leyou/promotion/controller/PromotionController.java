package com.leyou.promotion.controller;

import com.leyou.item.entity.Sku;
import com.leyou.promotion.dto.SkuDTO;
import com.leyou.promotion.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <功能简述><br>
 * <>
 *
 * @author DELL
 * @create 2020/4/6
 * @since 1.0.0
 */
@RestController
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    /**
     * 查询所有抢购商品
     */
    @GetMapping("/promotion/activeAll")
    public ResponseEntity<List<Sku>> findActivePromotion(){
        List<Sku> skus = promotionService.findActivePromotion();
        return ResponseEntity.ok(skus);
    }

    /**
     * 根据skuId查询sku信息
     */
    @GetMapping("/sku/{id}")
    public ResponseEntity<SkuDTO> findSkuById(@PathVariable("id")Long id){
        SkuDTO skuDTO = promotionService.findSkuById(id);
        return ResponseEntity.ok(skuDTO);
    }

    /**
     * 根据skuId生成订单
     */
    @GetMapping("/promotion/sale")
    public ResponseEntity<Long> SalePromotion(@RequestParam("id")Long id){
        Long orderId = promotionService.SalePromotion(id);
        return ResponseEntity.ok(orderId);
    }
}
