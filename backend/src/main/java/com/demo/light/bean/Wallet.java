package com.demo.light.bean;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    private Long userId;
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private String alipayUserId;
    private LocalDateTime bindAlipayTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
