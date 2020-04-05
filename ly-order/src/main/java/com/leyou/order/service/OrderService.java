package com.leyou.order.service;

import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.constant.LyConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.entity.Sku;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderStatusEnum;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.entity.Order;
import com.leyou.order.entity.OrderDetail;
import com.leyou.order.entity.OrderLogistics;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderLogisticsMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.utils.PayHelper;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PayHelper payHelper;


//    @GlobalTransactional
    public Long buildOrder(OrderDTO orderDTO) {
        try {
            //第一部分：保存订单信息
            //获取订单号
            Long orderId = idWorker.nextId();
            //获取当前用户的id
            Long userId = UserHolder.getUserId();
            //创建订单订单对象
            Order order = new Order();
            order.setOrderId(orderId);
            order.setStatus(OrderStatusEnum.INIT.value());
            order.setUserId(userId);
            order.setInvoiceType(0);
            order.setPaymentType(orderDTO.getPaymentType());
            order.setPostFee(0L);
            order.setActualFee(1L);//实付金额写一分钱方便测试

            //得到所有提交的购物车数据
            List<CartDTO> carts = orderDTO.getCarts();
            //将列表类型的CartDTO集合转成键值对的集合
            Map<Long, Integer> cartMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
            //收集到购物车数据中所有的sku的id
            List<Long> skuIds = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
            //根据Sku的id的集合查询出Sku对象的集合
            List<Sku> skus = itemClient.findSkusByIds(skuIds);
            //计算订单总金额
            Long totalFee = skus.stream().mapToLong(sku -> sku.getPrice() * cartMap.get(sku.getId())).sum();
            //给订单总金额赋值
            order.setTotalFee(totalFee);
            //保存订单
            orderMapper.insertSelective(order);

            //第二部分：保存订单详情信息
            //初始化订单详情集合对象
            List<OrderDetail> orderDetails = new ArrayList<>();
            //遍历第一部分获取到的sku对象列表
            skus.forEach(sku -> {
                //初始化一个OrderDetail对象
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setId(idWorker.nextId());
                orderDetail.setOrderId(orderId);
                orderDetail.setTitle(sku.getTitle());
                orderDetail.setSkuId(sku.getId());
                orderDetail.setPrice(sku.getPrice());
                orderDetail.setOwnSpec(sku.getOwnSpec());
                orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
                orderDetail.setNum(cartMap.get(sku.getId()));
                orderDetail.setCreateTime(new Date());
                orderDetail.setUpdateTime(new Date());
                //把OrderDetail对象添加到OrderDetail集合中
                orderDetails.add(orderDetail);
            });
            //保存订单详情列表
            orderDetailMapper.insertList(orderDetails);

            //第三部分：保存物流信息
            //获取用户的物流信息
            AddressDTO addressDTO = userClient.queryAddressById(userId, orderDTO.getAddressId());
            //将AddressDTO转成OrderLogistics
            OrderLogistics orderLogistics = BeanHelper.copyProperties(addressDTO, OrderLogistics.class);
            //设置订单号
            orderLogistics.setOrderId(orderId);
            //保存订单物流信息
            orderLogisticsMapper.insertSelective(orderLogistics);

            //第四部分：减库存
            itemClient.minusStock(cartMap);

            //遗留功能：清空购物车。什么时候清空？下单【对卖家不友好】，支付【对买家不友好】。
            return orderId;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    public OrderVO findOrderById(Long id) {
        try {
            //根据订单号查询订单
            Order order = orderMapper.selectByPrimaryKey(id);
            //将Order对象转成OrderVO对象
            OrderVO orderVO = BeanHelper.copyProperties(order, OrderVO.class);

            //根据订单号查询订单详情列表
            OrderDetail record = new OrderDetail();
            record.setOrderId(id);
            List<OrderDetail> orderDetails = orderDetailMapper.select(record);
            //把OrderDetail集合赋值给OrderVO的属性
            orderVO.setDetailList(orderDetails);

            //根据订单号查询物流信息
            OrderLogistics orderLogistics = orderLogisticsMapper.selectByPrimaryKey(id);
            orderVO.setLogistics(orderLogistics);

            return orderVO;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

    }

    public String getPayUrl(Long id) {
        //得到当前支付链接在redis中存储的key
        String payUrlRedisKey = LyConstants.PAY_URL_PRE+id;
        //判断当前订单的支付链接是否已经生成
        Boolean hasKey = redisTemplate.hasKey(payUrlRedisKey);
        //如果存在
        if(hasKey){
            return redisTemplate.opsForValue().get(payUrlRedisKey);
        }
        //如果不存在，我们可以查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        //判断订单状态是否已经支付
        if(!(order.getStatus()==OrderStatusEnum.INIT.value())){
            throw new LyException(501, "订单已支付，不要重复支付！");
        }
        //生成支付链接
        String payUrl = payHelper.getPayUrl(id, order.getActualFee());
        //将支付链接存储到redis中，有效期2小时
        redisTemplate.opsForValue().set(payUrlRedisKey, payUrl, 2, TimeUnit.HOURS);
        return payUrl;
    }

    public Integer queryOrderStatus(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if(order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        return order.getStatus();
    }

    public void handlerWxResp(Map<String, String> wxNotifyParams) {
        log.info("【微信回调通知】业务开始！");
        //校验通信标识和业务标识
        payHelper.checkWxResp(wxNotifyParams);
        //获取微信通知中的商户订单号
        Long orderId = Long.valueOf(wxNotifyParams.get("out_trade_no"));
        //获取微信通知中的支付金额
        Long totalFee = Long.valueOf(wxNotifyParams.get("total_fee"));
        //根据微信通知中的商户订单号查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //判断订单状态
        if(order.getStatus()!=OrderStatusEnum.INIT.value()){
            throw new LyException(501, "此订单已支付!");
        }
        //比对金额
        if(!totalFee.equals(order.getActualFee())){
            throw new LyException(501, "支付金额与订单应付金额不一致!");
        }
        //修改订单状态
        Order record = new Order();
        record.setOrderId(orderId);
        record.setStatus(OrderStatusEnum.PAY_UP.value());
        record.setPayTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(record);
        if(count!=1){
            //此处应该将订单状态更新失败的日志入库，后续有人工修改，保证正常给微信成功通知。
            log.error("【微信回调通知】修改订单状态失败！");
        }
        log.info("【微信回调通知】成功结束！");
    }
}