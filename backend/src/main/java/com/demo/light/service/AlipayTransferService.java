package com.demo.light.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public interface AlipayTransferService {
    String transfer(String account, BigDecimal amount, String outBizNo,String remark);
}
