package com.leyou.item.entity;

import lombok.Data;

import javax.persistence.Id;


@Data
public class CategoryGroup {

    @Id
    private Long groupId;
    private String categoryIds;
    private String names;


}
