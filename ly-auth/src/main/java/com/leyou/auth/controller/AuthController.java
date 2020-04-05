package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 认证
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        authService.login(username, password, request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户是否是已认证的状态
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(HttpServletRequest request,
                                           HttpServletResponse response){
        UserInfo userInfo = authService.verify(request, response);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response){
        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 微服务的授权功能
     * @param id       服务id
     * @param serviceName   服务名称
     * @return         给服务签发的token
     */
    @GetMapping("/authorization")
    public ResponseEntity<String> authorize(@RequestParam("id") Long id,
                                            @RequestParam("serviceName") String serviceName){
        return ResponseEntity.ok(authService.authorize(id, serviceName));
    }

}
