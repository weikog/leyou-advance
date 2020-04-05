package com.leyou.user.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Date;

@Table(name = "tb_user")
@Data
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Size(min = 4, max = 6, message = "用户名不符合规范")
    private String username;
    @Length(min = 4, max = 6, message = "密码不规范")
    private String password;
    private String phone;
    private Date createTime;
    private Date updateTime;
}