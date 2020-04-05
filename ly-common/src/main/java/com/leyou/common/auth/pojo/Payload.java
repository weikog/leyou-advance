package com.leyou.common.auth.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author 黑马程序员
 */
@Data
public class Payload<T> {
    private String id;//当前token的id
    private T userInfo;//用户信息，不能有密码
    private Date expiration;//过期时间
}
