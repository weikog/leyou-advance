package com.leyou.search.mq;

import com.leyou.common.constant.MQConstants;
import com.leyou.search.service.SearchService;
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
    private SearchService searchService;

    /**
     * 上架添加索引库
     * @param id  必须和消费的生产者中的消息类型一致
     */
    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = MQConstants.Queue.SEARCH_ITEM_UP, durable="true"),//监听的队列
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type= ExchangeTypes.TOPIC),//指定当前当前队列绑定的交换机
            key = MQConstants.RoutingKey.ITEM_UP_KEY
    ))
    public void indexWrite(Long id){
        searchService.indexWrite(id);
    }

    /**
     * 下架删除索引库
     * @param id  必须和消费的生产者中的消息类型一致
     */
    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = MQConstants.Queue.SEARCH_ITEM_DOWN, durable="true"),//监听的队列
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type= ExchangeTypes.TOPIC),//指定当前当前队列绑定的交换机
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void indexDelete(Long id){
        searchService.indexDelete(id);
    }

}
