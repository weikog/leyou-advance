package com.leyou.order.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下单
     */
    @PostMapping("/order")
    public ResponseEntity<Long> buildOrder(@RequestBody OrderDTO orderDTO){
        Long orderId = orderService.buildOrder(orderDTO);
        return ResponseEntity.ok(orderId);
    }

    /**
     * 订单查询
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<OrderVO> findOrderById(@PathVariable("id") Long id){
        OrderVO orderVO = orderService.findOrderById(id);
        return ResponseEntity.ok(orderVO);
    }

    /**
     * 生成支付链接
     */
    @GetMapping("/order/url/{id}")
    public ResponseEntity<String> getPayUrl(@PathVariable("id") Long id){
        String payUrl = orderService.getPayUrl(id);
        return ResponseEntity.ok(payUrl);
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/order/state/{id}")
    public ResponseEntity<Integer> queryOrderStatus(@PathVariable("id") Long id){
        Integer orderStatus = orderService.queryOrderStatus(id);
        return ResponseEntity.ok(orderStatus);
    }

    @GetMapping("/order/list")
    public ResponseEntity<PageResult<OrderVO>> findUserOrder(@RequestParam(value = "status",defaultValue = "0")Integer status,
                                                  @RequestParam(value = "page")Integer page,
                                                  @RequestParam(value = "rows")Integer rows){
        PageResult<OrderVO> orderVOList=orderService.findUserOrder(status,page,rows);
        return ResponseEntity.ok(orderVOList);
    }

}