package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

@RegisterMapper//标识当前为一个通用mapper的接口
public interface BaseMapper<T> extends Mapper<T>, IdsMapper<T>, IdListMapper<T, Long> {
}