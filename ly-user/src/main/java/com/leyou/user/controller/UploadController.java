package com.leyou.user.controller;

import com.leyou.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;



@Controller
public class UploadController {
    @Autowired
    private UserInfoService userInfoService;
    @PostMapping("/image")
    public ResponseEntity<Void> saveImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request, @RequestParam("id") Long id) {
        userInfoService.upload(multipartFile, request, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
