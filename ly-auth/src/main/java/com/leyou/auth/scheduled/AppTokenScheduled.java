package com.leyou.auth.scheduled;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppTokenScheduled {
    /**
     * token刷新间隔
     */
    private static final long TOKEN_REFRESH_INTERVAL = 86400000L;
    /**
     * token获取失败后重试的间隔
     */
    private static final long TOKEN_RETRY_INTERVAL = 10000L;
    /**
     * 提供一个存储token的属性
     */
    private String token;
    /**
     * 获取token相关配置信息
     */
    @Autowired
    private JwtProperties jwtProp;
    /**
     * 注入业务代码
     */
    @Autowired
    private AuthService authService;
    /**
     * 获取token定时任务，每24小时执行一次
     */
    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void autoAppAuth(){
        while (true){
            try {
                String token = authService.authorize(jwtProp.getApp().getId(), jwtProp.getApp().getServiceName());
                this.token = token;
                //控制台日志
                log.info("【{}微服务自动获取token】成功！",jwtProp.getApp().getServiceName());
                break;
            }catch (Exception e){
                log.error("【{}微服务自动获取token】失败！十秒钟后会再次获取！",jwtProp.getApp().getServiceName());
            }
            try {
                Thread.sleep(TOKEN_RETRY_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 提供一个获取token的方法
     */
    public String getToken() {
        return token;
    }
}
