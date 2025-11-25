package com.demo.light.repository;

import com.demo.light.bean.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    //这个是根据订单号来查找所有orderItem
    List<OrderItem> selectOrderItemByOrderId(String orderId);
    //插入一个新的orderItem
    void insertOrderItem(OrderItem orderItem);



}
