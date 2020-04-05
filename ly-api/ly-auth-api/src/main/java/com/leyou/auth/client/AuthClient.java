package com.leyou.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 1111111111
 * 2222222222222
 * 33333333333
 */
@FeignClient("auth-service")
public interface AuthClient {
    /**
     * 微服务的授权功能
     * @param id       服务id
     * @param serviceName   服务名称
     * @return         给服务签发的token
     */
    @GetMapping("/authorization")
    public String authorize(@RequestParam("id") Long id,
                            @RequestParam("serviceName") String serviceName);
}
