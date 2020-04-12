package com.leyou.user.service;


import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.user.config.JwtProperties;
import com.leyou.user.entity.UserInfo;
import com.leyou.user.mapper.AreaMapper;
import com.leyou.user.mapper.CityMapper;
import com.leyou.user.mapper.ProvinceMapper;
import com.leyou.user.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.06 21:24
 */
@Service
@Slf4j
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ProvinceMapper proMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private JwtProperties jwtProp;



    /**
     * 查询个人信息
     * @param userId
     * @return
     */
    public UserInfo findUserInfo(Long userId) {
        try {
            UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
            return userInfo;
        }catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
    }

    /**
     * 更新个人信息
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo) {
        data(userInfo);
    }

    /**
     * 更新头像
     * @param userInfo
     */
    public void saveUserImage(UserInfo userInfo) {
        data(userInfo);
    }

    /**
     * 数据处理
     * @param userInfo
     */
    private void data(UserInfo userInfo) {
        UserInfo data = findUserInfo(userInfo.getId());
        if (data == null) {
            //创建
            int count = userInfoMapper.insert(userInfo);
            if(count!=1){
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }else {
            //修改
            int count = userInfoMapper.updateByPrimaryKey(userInfo);
            if(count!=1){
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
    }



    /*
     * 查找用户信息
     * */
    public UserInfo queryUserInfo(Long id) {

        //根据用户id查询用户信息
        UserInfo ud = userInfoMapper.selectByPrimaryKey(id);
        if(ud==null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        return ud;
    }


    /*
     * 更新用户信息
     * */
    public void updateUserInfo(UserInfo ud) {
        //获取userId
        UserInfo userInfo = new UserInfo();
        Long userId = ud.getId();
        userInfo.setId(userId);
        //判断数据库是否有数据
        UserInfo ud1 = userInfoMapper.selectByPrimaryKey(userInfo);
        if(ud1==null){
            //插入用户信息
            int count = userInfoMapper.insertSelective(userInfo);
            if(count!=1){
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }else {
            //更新用户信息
            int count = userInfoMapper.updateByPrimaryKeySelective(userInfo);
            if (count != 1) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
    }


    // 图片上传的路径
    private final static String IMAGE_FILE_PATH = "D:\\project\\static\\images\\profile";
    // 图片 服务器名称
    private final static String IMAGE_URL = "http://image.leyou.com/images/profile/";
    // 允许上传图片的格式
    private final static List<String> ALLOW_UPLOAD_IMAGE = Arrays.asList("image/png","image/jpeg");

    /*
     * 头像上传
     * */
    public Map<Integer,String> upload(MultipartFile file, HttpServletRequest request, Long id) {
        // 判断图片上传的类型： png、jpeg、jpg图片
        if (!ALLOW_UPLOAD_IMAGE.contains(file.getContentType())) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        // 判断用户上传的图片是否是真实的图片
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
            }
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        // 图片上传的路径
        File imagePathFile = new File(IMAGE_FILE_PATH);
        // 图片的名称
        String imageName = UUID.randomUUID() + file.getOriginalFilename();
        try {
            /**
             * 参数一： 上传到那个目录
             * 参数二： 图片名称
             */
            file.transferTo(new File(imagePathFile, imageName));
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

        // 图片的访问路径
        String imageRoute = IMAGE_URL + imageName;
        //判断该用户在数据库书否已经添加了数据
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        UserInfo ud = userInfoMapper.selectOne(userInfo);
        userInfo.setImgurl(imageRoute);
        if(ud==null){
            int i = userInfoMapper.insertSelective(userInfo);
            if(i!=1){
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }else{
            int i = userInfoMapper.updateByPrimaryKeySelective(userInfo);
            if(i!=1){
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
        Map<Integer,String> map = new HashMap<>();
        map.put(1,"上传成功");
        map.put(2,imageRoute);
        return map;
    }
}
