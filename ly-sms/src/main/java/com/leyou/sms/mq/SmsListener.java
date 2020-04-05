package com.leyou.sms.mq;

import com.leyou.common.constant.MQConstants;
import com.leyou.sms.utils.SendMsgHelper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SendMsgHelper sendMsgHelper;

    /**
     * 监听验证码短信的消息队列，并给指定用户发送短信验证码
     * @param msgMap 约定此map中有两个键值对，key值为phone存手机号，key值为code存验证码
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.SMS_VERIFY_CODE_QUEUE, durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.SMS_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.VERIFY_CODE_KEY
    ))
    public void sendCheckCodeMsg(Map<String, String> msgMap){
        //获取手机号
        String phone = msgMap.get("phone");
        //获取验证码
        String code = msgMap.get("code");
        //执行发短信工具类
        sendMsgHelper.sendCheckCode(phone, code);
    }

}
