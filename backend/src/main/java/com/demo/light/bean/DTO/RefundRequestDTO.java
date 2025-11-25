package com.demo.light.bean.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestDTO {
    private String outTradeNo;
    private String outRequestNo;
    private BigDecimal refundAmount;
    private String reason;
}
