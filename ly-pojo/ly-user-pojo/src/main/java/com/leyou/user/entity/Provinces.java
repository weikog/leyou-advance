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
@Table(name = "tb_provinces")
public class Provinces {
    /**主键id*/
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    /**省id*/
    private Long provinceid;
    /**省名*/
    private String province;
}
