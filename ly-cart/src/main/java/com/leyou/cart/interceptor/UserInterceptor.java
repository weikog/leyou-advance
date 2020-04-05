package com.leyou.cart.interceptor;

import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.constant.LyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            //获取用户id
            Long user = Long.valueOf(request.getHeader(LyConstants.USER_HOLDER_KEY));
            //将用户id放入线程的局部变量中
            UserHolder.setUserId(user);
            log.info("【获取认证的用户id】成功！");
            return true;
        }catch (Exception e){
            log.error("【获取认证的用户id】失败！");
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserHolder.removeUserId();
    }

}
