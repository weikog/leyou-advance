package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    public void insertCategoryAndBrand(@Param("cids") List<Long> cids, @Param("bid") Long bid);

    @Select("SELECT b.* FROM tb_brand b, tb_category_brand cb " +
            "WHERE b.id = cb.brand_id AND cb.category_id = #{id}")
    public List<Brand> findBrandByCid(Long id);
}
