package com.leyou.gateway.filter;

import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.constant.LyConstants;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.leyou.gateway.scheduled.AppTokenScheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProp;

    @Autowired
    private FilterProperties filterProp;

    @Autowired
    private AppTokenScheduled appTokenScheduled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //通过上下文获取request域
        ServerHttpRequest request = exchange.getRequest();
        //通过上下文获取response域
        ServerHttpResponse response = exchange.getResponse();

        //修改request域，在任何网关发起的请求中添加请求头信息
        ServerHttpRequest newRequest = request.mutate().header(LyConstants.APP_TOKEN_HEADER, appTokenScheduled.getToken()).build();
        //修改网关的上下文exchange，修改上下文中的request域
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

        //获取当前请求的uri
        String path = request.getURI().getPath();
        //判断当前uri是否在白名单，白名单都是不需要登录的请求
        if(isAllowPath(path)){
            //成功通过当前过滤器，继续执行其他过滤器
            return chain.filter(newExchange);
        }
        //获取token
        String token = null;
        //解析token
        Payload<UserInfo> payload = null;
        try {
            token = request.getCookies().getFirst(jwtProp.getCookie().getCookieName()).getValue();
            payload = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey(), UserInfo.class);
        }catch (Exception e){
            //解析token失败，表示当前用户未登录
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //直接终止当前请求
            return response.setComplete();
        }

        //获取当前用户信息
        UserInfo userInfo = payload.getUserInfo();
        //获取用户id
        Long userId = userInfo.getId();
        //在请求头中添加用户id
        ServerHttpRequest newRequestUser = newRequest.mutate().header(LyConstants.USER_HOLDER_KEY, userId.toString()).build();
        //修改上下文
        ServerWebExchange newExchangeUser = newExchange.mutate().request(newRequestUser).build();
        //成功通过当前过滤器，继续执行其他过滤器
        return chain.filter(newExchangeUser);
    }

    //判断当前请求是否是白名单
    private Boolean isAllowPath(String path) {
        for (String allowPath : filterProp.getAllowPaths()) {
            if(path.contains(allowPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
