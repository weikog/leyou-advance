package com.leyou.user.service;

import com.leyou.common.constant.LyConstants;
import com.leyou.common.constant.MQConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Boolean checkData(String data, Integer type) {
        //提供查询条件对象
        User record = new User();
        //根据参数类型来封装查询条件
        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //查询数据库
        int count = userMapper.selectCount(record);
        return count==0;
    }

    public void sendCheckCode(String phone) {
        //生成六位纯数字验证码
        String code = RandomStringUtils.randomNumeric(6);
        //提供注册时短信验证码在redis中的key
        String key = LyConstants.REDIS_KEY_PRE+phone;
        //存入reids中一份验证码
        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.HOURS);
        //封装发送短信验证码的数据
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("phone", phone);
        msgMap.put("code", code);
        //给mq消息队列中发一份发送短信验证码的数据
        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME,
                MQConstants.RoutingKey.VERIFY_CODE_KEY, msgMap);
    }

    public void register(User user, String code) {
        //提供注册时短信验证码在redis中的key
        String key = LyConstants.REDIS_KEY_PRE+user.getPhone();
        //获取redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(key);
        //比对reids中的验证码和用户输入的验证码是否一致
        if(!StringUtils.equals(redisCode, code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //保存数据
        int count = userMapper.insertSelective(user);
        if(count!=1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public User findUserByNameAndPassword(String username, String password) {
        //先根据用户名查询用户
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        if(user==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //比对数据库中的密码和用户输入的密码是否是同一个
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if(!matches){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return user;
    }
}
