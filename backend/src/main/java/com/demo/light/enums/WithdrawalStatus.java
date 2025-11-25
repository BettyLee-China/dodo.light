package com.demo.light.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithdrawalStatus {
    PENDING("PENDING","待处理"),
    PROCESSING("PROCESSING","处理中"),
    SUCCESS("SUCCESS","提现成功"),
    FAILED("FAILED","提现失败");
    private final String status;
    private final String value;

    @Override
    public String toString() {
        return status.toString();
    }
}
