package com.leyou.common.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor//无参构造方法
@AllArgsConstructor//全参构造方法
public class UserInfo {

    private Long id;

    private String username;
    
    private String role;//角色【课程中也没用到】
}