package com.leyou.order.mq;

import com.leyou.common.constant.MQConstants;
import com.leyou.order.service.OrderService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <功能简述><br>
 * <>
 *
 * @author DELL
 * @create 2020/4/7
 * @since 1.0.0
 */
@Component
public class PromotionListener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.PROMOTION_ORDER_QUEUE,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.PROMOTION_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.PROMOTION_KEY
    ))
    public Long buildPromotionOrder(Map<String,Long> promotionMap){
        System.out.println("抢购---------消费");
        Long orderId = orderService.buildPromotionOrder(promotionMap);
        return orderId;
    }
}
