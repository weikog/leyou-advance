package com.leyou.user.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.09 15:02
 */
@Data
@Table(name = "tb_cities")
public class Cities {
    @Id
    @KeySql(useGeneratedKeys = true)
    /**主键id*/
    private Long id;
    /**城市id*/
    private Long cityid;
    /**城市名*/
    private String city;
    /**所属省id*/
    private Long provinceid;
}
