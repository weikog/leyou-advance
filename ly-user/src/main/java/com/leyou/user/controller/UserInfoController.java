package com.leyou.user.controller;


import com.leyou.user.config.JwtProperties;
import com.leyou.user.entity.UserInfo;
import com.leyou.user.service.AddressService;
import com.leyou.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.06 21:17
 */
@RestController
@Slf4j
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private JwtProperties prop;

    @Autowired
    private AddressService addressService;


    /*
     * 用户信息回显
     * */
    @GetMapping("/userInfo/info")
    public ResponseEntity<UserInfo> queryUserInfo(@RequestParam("id") Long id){
        UserInfo ud=userInfoService.queryUserInfo(id);
        return ResponseEntity.ok(ud);
    }

    /*
     * 更新用户信息
     * */
    @PostMapping("/userInfo/updateInfo")
    public ResponseEntity<Void> updateUserInfo(@RequestBody UserInfo userInfo, HttpServletRequest request){
        userInfoService.updateUserInfo(userInfo, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
