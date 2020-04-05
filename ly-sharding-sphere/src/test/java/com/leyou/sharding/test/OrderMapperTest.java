package com.leyou.sharding.test;

import com.leyou.sharding.entity.Order;
import com.leyou.sharding.mapper.OrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    // 插入测试
    @Test
    public void test(){
        Order order;
        int count;
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            order = new Order();
            order.setUserId(1L);// 使用工具类获取拦截器传递过来的用户id
            order.setStatus(1); // 订单状态
            order.setSourceType(2); // 订单来源 1:app端，2：pc端，3：微信端
            order.setPostFee(0L);// 邮费：全场包邮
            order.setPaymentType(1);// 支付类型：在线支付
            order.setInvoiceType(0);// 发票类型，0无发票，1普通发票，2电子发票，3增值税发票
            order.setActualFee(1L);//实际支付 = 总金额 - 活动金额 ； 这里为了测试我们写个1分
            order.setTotalFee(Long.valueOf(random.nextInt(1000)+i)); // 总金额

            count = orderMapper.insertSelective(order);
            System.out.println("插入条数："+count);
        }
    }

	// 查询所有
    @Test
    public void test2(){
        List<Order> items = orderMapper.selectAll();
        System.out.println("总记录数======>"+items.size());
        System.out.println(items.get(0));
    }

    // 根据id查询
    @Test
    public void test3(){
        Order item = orderMapper.selectByPrimaryKey(452770162538446848L);
        System.out.println(item);
    }

    // 更新
    @Test
    public void test4(){
        Order order = new Order();
        order.setOrderId(452770162538446848L);
        order.setUserId(2222L);
        order.setStatus(0);

        int count = orderMapper.updateByPrimaryKeySelective(order);
        System.out.println(count);

        order = orderMapper.selectByPrimaryKey(452770162538446848L);
        System.out.println(order);
    }

	// 删除
    @Test
    public void test5(){
        int count = orderMapper.deleteByPrimaryKey(452770162538446848L);
        System.out.println(count);
    }

}