package com.leyou.item.dto;

import com.leyou.item.entity.SpecParam;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SpecGroupDTO {

    private Long id;

    private Long cid;

    private String name;

    private Date createTime;

    private Date updateTime;

    private List<SpecParam> params;
}