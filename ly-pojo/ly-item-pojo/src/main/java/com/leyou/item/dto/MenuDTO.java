package com.leyou.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {

    private Long id;
    private String name;

    private List<CategoryDTO> thirCategorys;
}
