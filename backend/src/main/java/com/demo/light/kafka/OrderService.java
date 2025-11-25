package com.demo.light.kafka;

import com.demo.light.bean.Order;
import com.demo.light.bean.OrderRequest;
import com.demo.light.bean.VO.OrderDetailVO;
import com.demo.light.bean.VO.OrderResponse;
import com.demo.light.bean.VO.OrderVO;
import com.demo.light.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public interface OrderService {


    OrderDetailVO previewOrder(String orderId);

    Order createOrder(Long userId,OrderRequest request);

    Order createOrder(Long userId, Integer addressId, Long productId, Integer quantity);

    void processPaymentSuccess(String orderId);

    Order getOrderByOrderNo(String orderId);

    void setOrderStatus(String orderId, OrderStatus orderStatus);

    void handleRefundSuccess(String orderId, String outRequestNo, String refundAmount, Date gmtRefundPay);



    OrderDetailVO getOrderDetail(String orderId);

    List<OrderVO> getOrders(Long userId);
}
