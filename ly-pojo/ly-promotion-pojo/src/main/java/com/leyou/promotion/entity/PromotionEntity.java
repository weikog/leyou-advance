package com.leyou.promotion.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
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
@Table(name = "tb_promotion")
public class PromotionEntity {
    @Id
    private Long skuId;
    private Long coupon;
    private Date beginTime;
    private Date endTime;
    private Integer store;
    private Integer sold;
    private Integer enable;
}
