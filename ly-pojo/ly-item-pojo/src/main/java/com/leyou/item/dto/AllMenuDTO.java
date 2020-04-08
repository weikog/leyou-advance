package com.leyou.item.dto;

import lombok.Data;

import java.util.List;

@Data
public class AllMenuDTO {

    private Long groupId;
    private String categoryIds;
    private String names;

    private List<MenuDTO> menuDTOS;
}
