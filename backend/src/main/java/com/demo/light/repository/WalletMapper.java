package com.demo.light.repository;

import com.demo.light.bean.Wallet;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface WalletMapper {

    //钱包扣钱，提现
    void deductBalance(BigDecimal amount,Long userId);

    //钱包增加，有客户买商品
    void increaseBalance(BigDecimal amount,Long userId);


    void freezeBalance(Long photographerId,BigDecimal frozenAmount);

    //解冻，将冻结的资金清零，意味着已经成功提现
    void deductFrozenBalance(Long userId,BigDecimal frozenAmount);

    //解冻，但是提现失败。将冻结的钱返回到余额。
    void unfreezeBalance(Long userId,BigDecimal frozenAmount);

    //更新user_id
    void updateAlipayUserId(Long userId, String alipayUserId, LocalDateTime bindAlipayTime);

    Wallet selectByUserId(Long userId);

    void insertWallet(Wallet wallet);
}
