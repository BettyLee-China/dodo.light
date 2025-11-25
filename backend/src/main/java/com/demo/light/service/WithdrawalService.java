package com.demo.light.service;

import com.demo.light.bean.DTO.WithdrawRequest;

public interface WithdrawalService {

    void createWithdrawal(Long photographerId, WithdrawRequest request);

    void processWithdrawals();



}
