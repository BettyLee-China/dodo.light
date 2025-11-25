package com.demo.light.bean;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Refund {
    private Long id;
    private String orderId;//原始订单号
    private String refundNo;
    private String outRequestNo;
    private BigDecimal refundAmount;
    private String reason;
    private String status;
    private LocalDateTime refundTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
