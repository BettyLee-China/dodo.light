package com.demo.light.service;


import com.demo.light.bean.VO.CartVO;
import com.demo.light.result.R;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    //增加
    R<Object> addItem(Long userId, Long productId, Integer quantity);
    //更新数量
    R<Object> updateItem(Long userId,Long productId,Integer quantity,Boolean selected);


    //下单之后清除购物车内的商品
    void deleteItems(Long userId, List<Long> productIds);
    //删除商品（单个)
    R<Object> deleteItem(Long userId,Long productId);
    //查询购物车
    CartVO getCart(Long userId);


    //清除购物车中的所有商品
    R<Object> clearCart(Long userId);


}
