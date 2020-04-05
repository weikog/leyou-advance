package com.leyou.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.SpuDetail;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 黑马程序员
 */
@Data
public class SpuDTO {
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String name;// 名称
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Date createTime;// 创建时间
    private String categoryName; // 商品分类名称拼接
    private String brandName;// 品牌名称

    private List<Sku> skus;
    private SpuDetail spuDetail;
    /**
     * 方便同时获取3级分类
     * 在该方法上添加@JsonIgnore意思是，
     * 当前javaBean对象在转成json的时候
     * 不再考虑categoryIds属性
     * @return
     */
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return Arrays.asList(cid1, cid2, cid3);
    }
}