package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /*本地上传图片*/
    @PostMapping("/image")
    public ResponseEntity<String> uploadImageToNginx(@RequestParam("file") MultipartFile file){
        String imgUrl = uploadService.uploadImageToNginx(file);
        return ResponseEntity.ok(imgUrl);
    }

    /*编写阿里云OSS获取签名的处理器*/
    @GetMapping("/signature")
    public ResponseEntity<Map<String, Object>> getOssSignature(){
        Map<String, Object> signature = uploadService.getOssSignature();
        return ResponseEntity.ok(signature);
    }

}
