package com.leyou.user.entity;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;



@Data
@Table(name = "tb_areas")
public class Area {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private String areaId;
    private String area;
    private String cityId;
}