package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.entity.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> brandPageQuery(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //封装分页条件
        PageHelper.startPage(page, rows);
        //创建一个通用mapper封装复杂数据库操作的对象【如果有多个条件，之间都是and关系，并且不是模糊查询，那么就属于简单条件】
        Example example = new Example(Brand.class);
        //获取一个封装复杂条件的对象【只能封装查询条件】
        Example.Criteria criteria = example.createCriteria();
        //封装查询条件
        if(!StringUtils.isBlank(key)){
            criteria.orEqualTo("id", key);
            criteria.orLike("name", "%"+key+"%");
            criteria.orEqualTo("letter", key.toUpperCase());
        }
        //封装排序条件 setOrderByClause里面写原生的排序sql语句 比如select * from tb_brand where id=1 or name like xx order by xx desc这里这些order by之后的部分即可
        if(!StringUtils.isBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc ? "DESC" : "ASC"));
        }
        //数据库查询
        List<Brand> brands = brandMapper.selectByExample(example);
        //封装PageHelper的分页对象
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //封装自定义的分页对象
        PageResult<Brand> pageResult = new PageResult<>(pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getList());
        return pageResult;
    }

    public void saveBrand(Brand brand, List<Long> cids) {
        try {
            //保存品牌对象 加Selective的通用mapper方法底层会自动去null值
            brandMapper.insertSelective(brand);
            //维护中间表
            brandMapper.insertCategoryAndBrand(cids, brand.getId());
        }catch (Exception e){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public Brand findBrandById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> findBrandByCid(Long id) {
        List<Brand> list = brandMapper.findBrandByCid(id);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
