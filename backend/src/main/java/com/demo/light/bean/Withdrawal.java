package com.demo.light.bean;

import com.demo.light.enums.ChannelEnum;
import com.demo.light.enums.WithdrawalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Withdrawal {
    private Long id;
    private BigDecimal amount;
    private Long userId;
    private String alipayUserId;
    private String bankName;
    private String accountNo;
    private String accountName;
    private ChannelEnum channelEnum;
    private String txId;//生成的提现订单号，需要发送给第三方支付平台（支付宝）
    private String remark;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String outBizNo;

    public Withdrawal(Long userId, BigDecimal amount, String bankName, String accountNo, String accountName, ChannelEnum channelEnum, String txId, String remark, WithdrawalStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.userId = userId;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNo = accountNo;
        this.accountName = accountName;
        this.channelEnum = channelEnum;
        this.txId = txId;
        this.remark = remark;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
}
