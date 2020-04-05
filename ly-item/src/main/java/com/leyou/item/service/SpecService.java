package com.leyou.item.service;

import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpecService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> findSpecGroupsByCid(Long id) {
        //封装简单条件
        SpecGroup record = new SpecGroup();
        record.setCid(id);
        //数据库查询
        List<SpecGroup> specGroups = groupMapper.select(record);
        //判空
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specGroups;
    }

    public List<SpecParam> findSpecParams(Long gid, Long cid, Boolean searching) {
        //假如cid和gid都不传，就认为参数有误。
        if(gid==null && cid==null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        //封装条件
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setSearching(searching);
        //数据库查询
        List<SpecParam> specParams = paramMapper.select(record);
        if(CollectionUtils.isEmpty(specParams)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specParams;
    }

    public List<SpecGroupDTO> findSpecByCid(Long id) {
        try {
            //根据三级分类的id查询规格组集合
            List<SpecGroup> specGroups = findSpecGroupsByCid(id);
            //将SpecGroup集合转成SpecGroupDTO集合
            List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(specGroups, SpecGroupDTO.class);

            //方式三【大师级写法】
            //根据第三级分类的id查询出所有规格参数
            List<SpecParam> specParams = findSpecParams(null, id, null);
            //使用流对规格参数集合安装规格组id进行分组
            Map<Long, List<SpecParam>> specParamsMap = specParams.stream().collect(Collectors.groupingBy(SpecParam::getGroupId));
            specGroupDTOS.forEach(specGroupDTO -> {
                specGroupDTO.setParams(specParamsMap.get(specGroupDTO.getId()));
            });

            //方式二【专家级写法】
//            //根据第三级分类的id查询出所有规格参数
//            List<SpecParam> specParams = findSpecParams(null, id, null);
//            //将规格参数对象放入对应的规格组对象中去
//            specGroupDTOS.forEach(specGroupDTO -> {
//                specParams.forEach(specParam -> {
//                    if(specGroupDTO.getId().equals(specParam.getGroupId())){
//                        //表示当前规格参数对象是属于当前规格组对象的
//                        specGroupDTO.getParams().add(specParam);
//                    }
//                });
//            });

            //方式一【入门级写法】
//            specGroupDTOS.forEach(specGroupDTO -> {
//                List<SpecParam> specParams = findSpecParams(specGroupDTO.getId(), null, null);
//                specGroupDTO.setParams(specParams);
//            });
            return specGroupDTOS;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
    }
}
