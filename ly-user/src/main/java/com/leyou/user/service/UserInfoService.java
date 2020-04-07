package com.leyou.user.service;


import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.user.entity.UserInfo;
import com.leyou.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.06 21:24
 */
@Service
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * 保存个人信息
     * @param userInfo
     */
    public void saveUserInfo(UserInfo userInfo) {
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
     * 更新头像
     * @param userInfo
     */
    public void saveUserImage(UserInfo userInfo) {
        UserInfo data = findUserInfo(userInfo.getId());
        if (data == null) {
            //创建
            int count = userInfoMapper.insert(userInfo);
            if (count != 1) {
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }else {
            //修改
            int count = userInfoMapper.updateByPrimaryKey(userInfo);
            if (count != 1) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
    }
}
