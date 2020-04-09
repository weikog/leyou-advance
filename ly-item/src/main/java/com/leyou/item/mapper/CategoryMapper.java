package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryGroup;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT cg.group_id,GROUP_CONCAT(cg.category_id) category_ids,REPLACE(GROUP_CONCAT(c.name),',','、' ) `names` " +
            "FROM tb_category c JOIN tb_category_group cg " +
            "ON c.id = cg.category_id WHERE c.parent_id = 0 GROUP BY cg.group_id")
    public List<CategoryGroup> getCategoryGroups();
}
