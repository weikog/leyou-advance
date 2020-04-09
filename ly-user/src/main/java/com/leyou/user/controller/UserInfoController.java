package com.leyou.user.controller;


import com.leyou.user.entity.UserInfo;
import com.leyou.user.service.UserInfoService;
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
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 查询个人信息
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfo> findUserInfo(@RequestParam("id") Long id) {
        UserInfo userInfo = userInfoService.findUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }


    /**
     * 更新个人信息
     * @param userInfo
     * @return
     */
    @PostMapping("/user/info")
    public ResponseEntity<Void> saveUserInfo(@RequestBody UserInfo userInfo) {
        //拼接生日
        String birthday =
        request.getParameter("year") + "-" + request.getParameter("month") + "-" + request.getParameter("day");
        //设置生日
        userInfo.setBirthday(birthday);

        userInfoService.saveUserInfo(userInfo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新头像
     * @param userInfo
     * @return
     */
    @PostMapping("/user/img")
    public ResponseEntity<Void> updateImage(@RequestBody UserInfo userInfo) {
        userInfoService.saveUserImage(userInfo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
