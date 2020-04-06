package com.leyou.promotion.dto;

import lombok.Data;

import java.util.Date;

/**
 * <功能简述><br>
 * <>
 *
 * @author DELL
 * @create 2020/4/6
 * @since 1.0.0
 */
@Data
public class SkuDTO {
    private Long skuId;
    private Long coupon;
    private Date beginTime;
    private Date endTime;
    private Integer store;
    private Integer enable;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private Integer stock;
    private String ownSpec;// 商品特殊规格的键值对
    private String indexes;// 商品特殊规格的下标
    private Date createTime;// 创建时间
    private Date updateTime;// 最后修改时间

}
