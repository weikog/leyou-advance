package com.leyou.user.controller;


import com.leyou.user.entity.UserInfo;
import com.leyou.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.06 21:17
 */
@RestController
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 查询个人信息
     * @param UserId
     * @return
     */
    @GetMapping("/userinfo/{UserId}")
    public ResponseEntity<UserInfo> findUserInfo(Long UserId) {
        UserInfo userInfo = userInfoService.findUserInfo(UserId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 保存个人信息
     * @param userInfo
     * @return
     */
    @PostMapping("/userinfo/save")
    public ResponseEntity<Void> saveUserInfo(UserInfo userInfo) {
        userInfoService.saveUserInfo(userInfo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 更新头像
     * @param userInfo
     * @return
     */
    public ResponseEntity<Void> updateImage(UserInfo userInfo) {
        userInfoService.saveUserImage(userInfo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
