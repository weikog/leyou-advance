package com.leyou.user.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Royo.Liu
 * @project_name leyou-advance
 * Created by 2020.04.09 15:03
 */
@Data
@Table(name = "tb_areas")
public class Areas {
    /**主键id*/
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    /**区县id*/
    private Long areaid;
    /**区县名*/
    private String area;
    /**所属城市id*/
    private Long cityid;
}
