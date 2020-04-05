package com.leyou.gateway.scheduled;

import com.leyou.auth.client.AuthClient;
import com.leyou.gateway.config.JwtProperties;
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
     * 调用获取token的feign接口
     */
    @Autowired
    private AuthClient authClient;
    /**
     * 获取token定时任务，每24小时执行一次
     */
    @Scheduled(fixedDelay = TOKEN_REFRESH_INTERVAL)
    public void autoAppAuth(){
        while (true){
            try {
                //调用远程feign接口获取token
                String token = authClient.authorize(jwtProp.getApp().getId(), jwtProp.getApp().getServiceName());
                //把token赋值给当前存储属性
                this.token = token;
                //控制台日志
                log.info("【{}微服务自动获取token】成功！",jwtProp.getApp().getServiceName());
                //跳出当前死循环
                break;
            }catch (Exception e){
                log.error("【{}微服务自动获取token】失败！十秒钟后会再次获取！",jwtProp.getApp().getServiceName());
            }
            try {
                //保证重试是十秒钟一次
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
