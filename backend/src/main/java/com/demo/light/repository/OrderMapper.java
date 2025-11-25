package com.demo.light.repository;

import com.demo.light.bean.Order;
import com.demo.light.enums.OrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> selectTimeoutOrders(@Param("timeoutTime")LocalDateTime timeoutTime);

    int updateById(Order order);

    Order selectOrderByOrderNo(String orderId);

    void updateOrderStatus(String orderId, OrderStatus orderStatus);
    //将一个订单保存到数据库
    void insertOrder(Order order);
    //写入付款时间等信息

    void updatePayTime(String orderId,LocalDateTime payTime);



    //获取某个用户的所有orders
    List<Order> selectOrdersByUserId(Long userId);


}
