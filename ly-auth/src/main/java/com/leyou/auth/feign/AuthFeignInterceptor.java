package com.leyou.auth.feign;

import com.leyou.auth.scheduled.AppTokenScheduled;
import com.leyou.common.constant.LyConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthFeignInterceptor implements RequestInterceptor {

    @Autowired
    private AppTokenScheduled appTokenScheduled;

    @Override
    public void apply(RequestTemplate template) {
        //在feign的请求中添加请求头信息
        template.header(LyConstants.APP_TOKEN_HEADER, appTokenScheduled.getToken());
    }
}
