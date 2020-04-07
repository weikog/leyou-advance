package com.leyou.order.test;

import com.leyou.order.entity.Order;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;

    @Test
    public void updateTest(){
        //修改订单状态
        Order record = new Order();
        record.setOrderId(1245640157269463041L);
        record.setStatus(2);
        record.setPayTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(record);
    }

   @Test
    public void promotionOrderTest(){
       Map<String,Long> map = new HashMap<>();
       map.put("userId",32L);
       map.put("id",2868393L);
       Long orderId = orderService.buildPromotionOrder(map);
       System.out.println(orderId);
   }



}
