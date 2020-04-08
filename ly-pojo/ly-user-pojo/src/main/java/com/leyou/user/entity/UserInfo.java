package com.leyou.user.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.06 21:10
 */
@Table(name = "tb_user_info")
@Data
public class UserInfo {
    /**主键*/
    @Id
    private Long id;
    /**昵称*/
    private String name;
    /**性别*/
    private Boolean gender;
    /**生日*/
    private String birthday;
    /**省市县*/
    private String place;
    /**职业*/
    private String occupation;
    /**头像地址*/
    private String img_url;
}
