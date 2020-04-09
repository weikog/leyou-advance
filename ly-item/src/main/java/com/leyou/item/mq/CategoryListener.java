package com.leyou.item.mq;

import com.leyou.common.constant.MQConstants;
import com.leyou.item.service.CategoryService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryListener {
    @Autowired
    private CategoryService categoryService;

    /**
     * 商品分类修改后，更新首页导航菜单数据到redis
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = MQConstants.Queue.CATEGORY_UPDATE,
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = MQConstants.Exchange.CATEGORY_EXCHANGE_NAME,
                    type = ExchangeTypes.TOPIC
            ),
            key = MQConstants.RoutingKey.CATEGORY_UPDATE_KEY
    ))
    public void updateMenuRedis(Long id){
        categoryService.addRedis();
    }

}











