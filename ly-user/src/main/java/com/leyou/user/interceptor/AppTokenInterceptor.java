package com.leyou.user.interceptor;

import com.leyou.common.auth.pojo.AppInfo;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.constant.LyConstants;
import com.leyou.user.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AppTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProp;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = request.getHeader(LyConstants.APP_TOKEN_HEADER);
        if(StringUtils.isBlank(token)){
            log.error("【服务鉴权】未通过！来访服务的token不存在！");
            //阻止访问处理器
            return false;
        }
        //解析token
        Payload<AppInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey(), AppInfo.class);
            //获取当前服务的token的服务信息
            AppInfo appInfo = payload.getUserInfo();
            //获取服务可以访问的服务列表
            List<Long> targetList = appInfo.getTargetList();
            //判断当前请求是否有访问权限
            if(CollectionUtils.isEmpty(targetList) || !targetList.contains(jwtProp.getApp().getId())){
                log.error("【服务鉴权】未通过！来访服务没有访问权限！");
                //阻止访问处理器
                return false;
            }
        }catch (Exception e){
            log.error("【服务鉴权】未通过！来访服务的token不合法！");
            //阻止访问处理器
            return false;
        }
        //有访问权限，顺利通过拦截器
        return true;
    }
}
