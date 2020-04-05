package com.leyou.user.controller;

import com.leyou.common.exception.pojo.LyException;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 数据校验
     * @param data 要校验的数据
     * @param type 要校验的数据类型：1，用户名；2，手机
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type){
        Boolean isCanUse = userService.checkData(data, type);
        return ResponseEntity.ok(isCanUse);
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCheckCode(@RequestParam("phone") String phone){
        userService.sendCheckCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user,
                                         BindingResult result,//此对象要紧挨着需要被校验的对象
                                         @RequestParam("code") String code){
        if(result.hasErrors()){
            //收集异常信息
            String errorStr = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("|"));
            //抛出获取异常信息
            throw new LyException(400, errorStr);
        }
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户信息
     */
    @GetMapping("/query")
    public ResponseEntity<User> findUserByNameAndPassword(@RequestParam("username") String username,
                                                          @RequestParam("password") String password){
        User user = userService.findUserByNameAndPassword(username, password);
        return ResponseEntity.ok(user);
    }

}
