package com.leyou.promotion.service;

import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.constant.LyConstants;
import com.leyou.common.constant.MQConstants;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.entity.Sku;
import com.leyou.promotion.dto.SkuDTO;
import com.leyou.promotion.entity.PromotionEntity;
import com.leyou.promotion.mapper.PromotionMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <功能简述><br>
 * <>
 *
 * @author DELL
 * @create 2020/4/6
 * @since 1.0.0
 */
@Service
@Transactional
public class PromotionService {
    @Autowired
    private PromotionMapper promotionMapper;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    //查询所有抢购商品
    public List<Sku> findActivePromotion() {
        List<PromotionEntity> promotionEntities = promotionMapper.selectAll();
        //抢购商品为空
        if (promotionEntities == null) {
            throw new LyException(501, "不存在抢购商品");
        }
        //将skuId获取成集合
        List<Long> skuIds = promotionEntities.stream()
                .map(PromotionEntity::getSkuId)
                .collect(Collectors.toList());
        //调用itemClient进行查询对应的skus
        List<Sku> skus = itemClient.findSkusByIds(skuIds);
        return skus;
    }


    //根据skuId查询skuDTO
    public SkuDTO findSkuById(Long id) {
        //创建SkuDTO
        SkuDTO skuDTO = new SkuDTO();
        //根据skuId查询出sku
        Sku sku = itemClient.findSkuById(id);
        //将sku对应信息放入skuDTO中
        BeanUtils.copyProperties(sku, skuDTO);
        //根据skuId查询出抢购商品
        PromotionEntity promotionEntity = promotionMapper.selectByPrimaryKey(id);
        //将抢购商品对应的信息放入skuDTO中
        BeanUtils.copyProperties(promotionEntity, skuDTO);
        return skuDTO;
    }

    //根据skuId生成订单
    public Long SalePromotion(Long id) {
        //获取redis中抢购商品的key
        String key = LyConstants.SKU_PRE+id;
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //判断计数器是否存在
        if (! redisTemplate.hasKey(key)){
            redisTemplate.opsForValue().set(key, String.valueOf(0));
        }
        String number = redisTemplate.opsForValue().get(key);
        Long count = Long.valueOf(number);
        //判断是否已经销售完毕
        if (count<promotionMapper.selectByPrimaryKey(id).getStore()){
            ++count;
            Map<String,Long> map = new HashMap<>();
            map.put("skuId",id);
            map.put("userId",userId);
            redisTemplate.opsForValue().set(key, String.valueOf(count));
            amqpTemplate.convertAndSend(MQConstants.Exchange.PROMOTION_EXCHANGE_NAME,MQConstants.RoutingKey.PROMOTION_KEY,map);
        }
        return null;
    }
}
