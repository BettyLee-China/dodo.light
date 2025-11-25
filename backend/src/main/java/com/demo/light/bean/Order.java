package com.demo.light.bean;

import com.demo.light.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//主订单
public class Order {
    private String orderId;
    private Long userId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;

    // 收货信息（快照）
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;

    // 时间戳
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliverTime;
    private LocalDateTime receiveTime;
    private LocalDateTime cancelTime;
    private LocalDateTime closeTime;

    // 退款记录
    private BigDecimal refundAmount;
    private LocalDateTime refundTime;
    private String outRequestNo;
}
