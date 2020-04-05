package com.leyou.auth.mapper;

import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.common.mapper.BaseMapper;

import java.util.List;

public interface ApplicationInfoMapper extends BaseMapper<ApplicationInfo> {
    List<Long> queryTargetIdList(Long serviceId);
}
