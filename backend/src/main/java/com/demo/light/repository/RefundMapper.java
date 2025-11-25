package com.demo.light.repository;

import com.demo.light.bean.Refund;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefundMapper {
    //根据订单号查询所有退款记录
    Refund selectByOrderId(String orderId);
    //根据外部请求号查询
    Refund selectByOutRequestNo(String outRequestNo);

    //创建退款记录
    int insertRefund(Refund refund);

    void updateStatus(String outRequestNo,String status);





}
