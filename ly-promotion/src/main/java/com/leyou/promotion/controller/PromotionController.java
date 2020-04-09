package com.leyou.promotion.controller;

import com.leyou.promotion.dto.SkuDTO;
import com.leyou.promotion.entity.PromotionEntity;
import com.leyou.promotion.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<SkuDTO>> findActivePromotion(){
        List<SkuDTO> skuDTOS = promotionService.findActivePromotion();
        return ResponseEntity.ok(skuDTOS);
    }

    /**
     * 根据skuId查询sku信息
     */
    @GetMapping("/promotion/sku/{id}")
    public ResponseEntity<SkuDTO> findSkuById(@PathVariable("id")Long id){
        SkuDTO skuDTO = promotionService.findSkuById(id);
        return ResponseEntity.ok(skuDTO);
    }

    /**
     * 根据skuId生成订单
     */
    @GetMapping("/promotion/sale/{id}")
    public ResponseEntity<Long> SalePromotion(@PathVariable("id")Long id){
        Long orderId = promotionService.SalePromotion(id);
        System.out.println("--------------"+orderId.toString());
        if (orderId<0){
            return ResponseEntity.ok(-1L);
        }
        return ResponseEntity.ok(orderId);
    }

    /**
     * 减去抢购商品的库存
     */
    @PostMapping("/promotion/minusStore")
    public ResponseEntity<Void> minusStore(@RequestParam("id")Long id){
        promotionService.minusStore(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 添加抢购商品
     */
    @PostMapping("promotion/add")
    public ResponseEntity<Void> addPromotion(@RequestBody PromotionEntity promotionEntity){
        promotionService.addPromotion(promotionEntity);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
