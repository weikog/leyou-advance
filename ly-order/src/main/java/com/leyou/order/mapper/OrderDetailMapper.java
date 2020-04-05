package com.leyou.order.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.order.entity.OrderDetail;
import tk.mybatis.mapper.additional.insert.InsertListMapper;

public interface OrderDetailMapper extends BaseMapper<OrderDetail>, InsertListMapper<OrderDetail> {
}