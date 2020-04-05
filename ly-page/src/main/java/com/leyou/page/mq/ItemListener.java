package com.leyou.page.mq;

import com.leyou.common.constant.MQConstants;
import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.PAGE_ITEM_UP, durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void addStaticPage(Long id){
        pageService.createStaticItemPage(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.PAGE_ITEM_DOWN, durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void delStaticPage(Long id){
        pageService.delStaticPage(id);
    }

}
