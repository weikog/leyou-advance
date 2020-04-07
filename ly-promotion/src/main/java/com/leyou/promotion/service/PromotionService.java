package com.leyou.promotion.service;

import com.leyou.common.constant.LyConstants;
import com.leyou.common.constant.MQConstants;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
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

    private Object receive;

    //查询所有抢购商品
    public List<SkuDTO> findActivePromotion() {
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
        //将所有信息封装成SkuDTO
        List<SkuDTO>skuDTOS = BeanHelper.copyWithCollection(promotionEntities,SkuDTO.class);
        for (SkuDTO skuDTO : skuDTOS){
            for (Sku sku : skus){
                if (skuDTO.getSkuId().equals(sku.getId())){
                    BeanUtils.copyProperties(sku,skuDTO);
                }
                }
        }
        return skuDTOS;
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
    public Object SalePromotion(Long id,Long userId) {
        //获取到抢购商品的redis前缀
        String key = LyConstants.SKU_PRE+id;
        //使用redis计数器
        Long count = redisTemplate.opsForValue().increment(key, 0);
        //获取到抢购商品的限购数量
        PromotionEntity promotionEntity = promotionMapper.selectByPrimaryKey(id);
        Integer store = promotionEntity.getStore();
        //获取到用户id
//        Long userId = UserHolder.getUserId();
        //合成消息队列的发送信息
        Map<String,Long> promotionMap = new HashMap<>();
        promotionMap.put("id",id);
        promotionMap.put("userId",userId);
        if (count<=store){
             receive = amqpTemplate.convertSendAndReceive(MQConstants.Exchange.PROMOTION_EXCHANGE_NAME,
                    MQConstants.RoutingKey.PROMOTION_KEY, promotionMap);
        }
        return receive;
    }


    //减抢购商品库存
    public void minusStore(Long id) {
        //获取到当前的剩余库存
        PromotionEntity promotionEntity = promotionMapper.selectByPrimaryKey(id);
        //创建新的promotionEntity
        PromotionEntity newPro = new PromotionEntity();
        newPro.setStore(promotionEntity.getStore()-1);
        newPro.setSkuId(id);
        //减库存
        promotionMapper.updateByPrimaryKeySelective(newPro);
    }
}
