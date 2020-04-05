package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;
import com.leyou.common.auth.pojo.UserHolder;
import com.leyou.common.constant.LyConstants;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 购物车数据结构为：<key, <hashKey, Cart>>
     * @param cart
     */
    public void addCart(Cart cart) {
        //得到当前用户id
        Long userId = UserHolder.getUserId();
        //得到购物车的key
        String cartKey = LyConstants.CART_PRE+userId;
        //得到当前用户的购物车数据
        BoundHashOperations<String, String, String> cartHashMap = redisTemplate.boundHashOps(cartKey);
        //获取当前新添加的购物车对象的hashKey
        String hashKey = cart.getSkuId().toString();
        //判断当前新添加的购物车对象是否在原来的购物车集合中
        if(cartHashMap.hasKey(hashKey)){
            //如果新添加的购物车对象已经存在于用户的购物车列表，则获取购物车列表中对应skuId的购物车对象
            String oldCartStr = cartHashMap.get(hashKey);
            //将字符串格式的购物车对象转成对象格式
            Cart oldCart = JsonUtils.toBean(oldCartStr, Cart.class);
            //将原来购物车对象的数量合并到新添加的购物车对象中
            cart.setNum(cart.getNum()+oldCart.getNum());
        }
        //更新购物车
        cartHashMap.put(hashKey, JsonUtils.toString(cart));
    }

    public List<Cart> findCarts() {
        //得到当前用户id
        Long userId = UserHolder.getUserId();
        //得到购物车的key
        String cartKey = LyConstants.CART_PRE+userId;
        //判断当前用户是否有购物车
        if(!redisTemplate.hasKey(cartKey)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        //得到购物车对象
        BoundHashOperations<String, String, String> cartHashMap = redisTemplate.boundHashOps(cartKey);
        //得到购物车对象中所有的购物车列表
        List<String> carts = cartHashMap.values();
        if(CollectionUtils.isEmpty(carts)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        //将字符串格式的购物车对象集合转成对象列表
        List<Cart> cartList = carts.stream()
                .map(cart -> JsonUtils.toBean(cart, Cart.class))//把每个字符串格式的购物车对象转成Cart对象
                .collect(Collectors.toList());
        return cartList;
    }

    public void addCarts(List<Cart> carts) {
        for (Cart cart : carts) {
            addCart(cart);
        }
    }
}
