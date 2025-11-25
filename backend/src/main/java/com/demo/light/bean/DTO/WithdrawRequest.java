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
public class WithdrawRequest {
    private BigDecimal amount;
    private String channel;
    private String accountNo;
    private String accountName;
    private String remark;

}
