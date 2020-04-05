package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.leyou.common.exception.pojo.LyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private WXPayConfigImpl payConfig;

    /**
     * 微信统一下单工具类
     * @param orderId 商户订单号
     * @param actualFee 支付金额
     * @return 返回值是code_url
     */
    public String getPayUrl(Long orderId, Long actualFee){
        log.info("【统一下单工具类】开始执行！");
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "乐优支付");
        data.put("out_trade_no", orderId.toString());
        data.put("total_fee", actualFee.toString());
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", payConfig.getNotifyUrl());
        data.put("trade_type", payConfig.getPayType());  // 此处指定为扫码支付

        try {
            Map<String, String> resp = wxPay.unifiedOrder(data);
            //校验微信统一下单返回结果
            checkWxResp(resp);
            //返回code_url
            log.info("【统一下单工具类】成功结束！");
            return resp.get("code_url");
        } catch (Exception e) {
            log.error("【统一下单工具类】异常！异常信息为：{}", e.getMessage());
            throw new LyException(501, "【统一下单工具类】异常！");
        }
    }

    /*校验通信标识和业务标识*/
    public void checkWxResp(Map<String, String> resp) {
        //获取通信标识
        String returnCode = resp.get("return_code");
        //校验通信标识
        if(StringUtils.equals(returnCode, "FAIL")){
            log.error("【统一下单工具类】通信异常！异常信息为：{}", resp.get("return_msg"));
            throw new LyException(501, resp.get("return_msg"));
        }
        //获取业务标识
        String resultCode = resp.get("result_code");
        //校验业务标识
        if(StringUtils.equals(resultCode, "FAIL")){
            log.error("【统一下单工具类】业务异常！异常信息为：{}", resp.get("err_code_des"));
            throw new LyException(501, resp.get("err_code_des"));
        }
    }

}
