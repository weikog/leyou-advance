package com.leyou.item.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category_group")
@Data
public class CategoryGroup {

    @Id
    @KeySql(useGeneratedKeys=true)
    private Long groupId;
    private String categoryIds;
    private String names;


}
