package com.leyou.user.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Table(name = "tb_provinces")
public class Province {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Integer id;

    private String provinceId;
    private String province;
}
