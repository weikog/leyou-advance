package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.constant.LyConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.upload.config.OSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class UploadService {

    @Autowired
    private OSS client;

    @Autowired
    private OSSProperties prop;

    /*指定一个允许上传的图片的mime类型的集合*/
    private List<String> ALLOW_IMAGE_TYPE = Arrays.asList("image/jpeg");


    public String uploadImageToNginx(MultipartFile file) {

        //获取要上传的图片的mime类型
        String contentType = file.getContentType();
        //判断文件类型是否合法
        if(!ALLOW_IMAGE_TYPE.contains(contentType)){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //判断图片流是否为空
        if(bufferedImage==null){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

        //指定上传的图片名称
        String imageName = UUID.randomUUID()+file.getOriginalFilename();
        //指定图片上次的文件夹
        File imagePathFile = new File(LyConstants.IMAGE_PATH);
        //上传图片
        try {
            file.transferTo(new File(imagePathFile, imageName));
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
        return LyConstants.IMAGE_URL+imageName;
    }

    public Map<String, Object> getOssSignature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<String, Object>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);//签名
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);//单位是秒
            return respMap;
        } catch (Exception e) {
            log.error("阿里云OSS获取签名失败！失败原因为：{}", e.getMessage());
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
    }
}
