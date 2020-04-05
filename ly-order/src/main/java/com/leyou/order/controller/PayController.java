package com.leyou.order.controller;

import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PayController {

    @Autowired
    private OrderService orderService;

    /**
     * 此处理器是有微信来访问的，所以一些规则按照微信的来
     * 微信要求我们必须返回xml格式的数据
     * 只要处理器上有@ResponseBody，并且项目中有解析xml的jar包，而且要指定返回xml的数据，就可以返回xml数据了
     */
    @PostMapping(value = "/pay/wx/notify", produces = "application/xml")
    public Map<String, String> handlerWxResp(@RequestBody Map<String, String> wxNotifyParams){
        orderService.handlerWxResp(wxNotifyParams);
        //按照微信的要求给其返回成功接收到通知的信息
        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        return result;
    }

}
