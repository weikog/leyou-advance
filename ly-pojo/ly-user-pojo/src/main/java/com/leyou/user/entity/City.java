package com.leyou.user.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Table(name = "tb_cities")
public class City {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private String cityid;
    private String city;
    private String provinceid;
}
