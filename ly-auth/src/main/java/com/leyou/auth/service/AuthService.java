package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import com.leyou.common.auth.pojo.AppInfo;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private JwtProperties jwtProp;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /*认证业务代码*/
    public void login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        //校验用户名和密码是否正确
        User user = userClient.findUserByNameAndPassword(username, password);
        if(user==null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        //封装jwt中载荷中的用户对象
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), "admin");
        //构建token并写入浏览器的cookie中
        createTokenWriteToCookie(request, response, userInfo);
    }

    /*构建token并写入浏览器的cookie中*/
    private void createTokenWriteToCookie(HttpServletRequest request, HttpServletResponse response, UserInfo userInfo) {
        //生成token
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProp.getPrivateKey(), jwtProp.getCookie().getExpire());
        //把token写入浏览器的cookie中
        CookieUtils.newCookieBuilder()
                .request(request)
                .response(response)
                .name(jwtProp.getCookie().getCookieName())
                .value(token)
                .httpOnly(true)
                .domain(jwtProp.getCookie().getCookieDomain())
                .build();
    }

    /*校验用户的认证状态*/
    public UserInfo verify(HttpServletRequest request, HttpServletResponse response) {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookie().getCookieName());
        //解析token
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey(), UserInfo.class);
        }catch (Exception e){
            //如果解析失败，表示用户没有登录，抛出异常
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        //查看当前token是否在黑名单
        if(redisTemplate.hasKey(payload.getId())){
            //如果redis中有当前token的id，就认为当前token已经失效了
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        //获取载荷中的用户信息
        UserInfo userInfo = payload.getUserInfo();
        //得到认证token的过期时间
        Date expDate = payload.getExpiration();
        //得到一个刷新时间点
        DateTime refreshDateTime = new DateTime(expDate).minusMinutes(jwtProp.getCookie().getRefreshTime());
        //如果刷新时间点在当前时间之前就刷新认证token
        if (refreshDateTime.isBeforeNow()) {
            createTokenWriteToCookie(request, response, userInfo);
        }
        return userInfo;
    }

    /*退出登录的业务代码*/
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookie().getCookieName());
        //校验token
        Payload<Object> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
            //获取当前要退出的用户的token的id
            String tokenId = payload.getId();
            //获取当前token的过期时间
            Date expDate = payload.getExpiration();
            //得到距离过期时间还剩余的毫秒数
            long remainTime = expDate.getTime() - System.currentTimeMillis();
            //将剩余时间超过6秒的token放入黑名单，其余的不放也差不多该挂了
            if(remainTime>6000){
                redisTemplate.opsForValue().set(tokenId, "1", remainTime, TimeUnit.MILLISECONDS);
            }
        }catch (Exception e){
            //标识当前要退出登录的用户的token已经失效了，无需任何操作
            log.info("当前要退出登录的用户token已经失效！");
        }
        //无论token是否有效，都要删除token
        CookieUtils.deleteCookie(jwtProp.getCookie().getCookieName(),
                jwtProp.getCookie().getCookieDomain(), response);

    }

    /*根据服务id和服务名称查询服务是否存在*/
    public Boolean isUsable(Long id, String serviceName){
        //根据服务id查询服务对象
        ApplicationInfo applicationInfo = applicationInfoMapper.selectByPrimaryKey(id);
        if(applicationInfo==null){
            //服务id不对，直接返回false
            return false;
        }
        //验证服务名称是否正确
        if(!passwordEncoder.matches(serviceName, applicationInfo.getSecret())){
            //服务名称不对，返回false
            return false;
        }
        return true;
    }

    public String authorize(Long id, String serviceName) {
        //校验用户信息是否可用
        if(!isUsable(id, serviceName)){
            log.error("【服务器申请token】异常！服务id或者服务名称不正确!");
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        //查询出当前服务所能访问的服务id列表
        List<Long> serviceIds = applicationInfoMapper.queryTargetIdList(id);
        //创建返回token的服务信息存储对象
        AppInfo appInfo = new AppInfo(id, serviceName, serviceIds);
        //生成token
        String token = JwtUtils.generateTokenExpireInMinutes(appInfo, jwtProp.getPrivateKey(), jwtProp.getApp().getExpire());
        return token;
    }
}
