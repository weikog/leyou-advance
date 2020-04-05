package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsConstants;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SendMsgHelper {

    @Autowired
    private SmsProperties smsProp;

    @Autowired
    private IAcsClient client;

    /**
     * 验证码类短信发送
     * @param phone 手机号
     * @param code 验证码
     */
    public void sendCheckCode(String phone, String code){
        log.info("【阿里云验证码短信】工具类开始调用！");
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(smsProp.getDomain());
        request.setVersion(smsProp.getVersion());
        request.setAction(smsProp.getAction());
        request.putQueryParameter(SmsConstants.SMS_PARAM_REGION_ID, smsProp.getRegionID());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_PHONE, phone);
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_SIGN_NAME, smsProp.getSignName());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_CODE, smsProp.getVerifyCodeTemplate());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_PARAM, "{\""+smsProp.getCode()+"\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            //将短信的返回结果转成map
            Map<String, String> respMap = JsonUtils.toMap(response.getData(), String.class, String.class);
            //判断阿里云SMS短信发送是否成功
            if(StringUtils.equals(respMap.get(SmsConstants.SMS_RESPONSE_KEY_CODE), SmsConstants.OK)){
                log.info("【阿里云验证码短信】工具调用成功完成！");
                return;
            }else {
                log.error("【阿里云验证码短信】工具调用失败！失败原因为：{}", respMap.get(SmsConstants.SMS_RESPONSE_KEY_MESSAGE));
                //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
                throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
            }
        } catch (ServerException e) {
            log.error("【阿里云验证码短信】工具调用失败！阿里云的SMS出现异常了！异常信息为：{}", e.getMessage());
            //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
            throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);

        } catch (ClientException e) {
            log.error("【阿里云验证码短信】工具调用失败！我们的短信工具类代码异常了！异常信息为：{}", e.getMessage());
            //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
            throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
        }
    }

}
