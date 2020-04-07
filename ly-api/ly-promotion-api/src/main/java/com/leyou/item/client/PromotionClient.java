package com.leyou.item.client;

import com.leyou.promotion.dto.SkuDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("promotion-service")
public interface PromotionClient {
    /**
     * 根据skuId查询sku信息
     */
    @GetMapping("/sku/{id}")
    public SkuDTO findSkuById(@PathVariable("id")Long id);

    /**
     * 减去抢购商品的库存
     */
    @PostMapping("/promotion/minusStore")
    public void minusStore(@RequestParam("id")Long id);
}
