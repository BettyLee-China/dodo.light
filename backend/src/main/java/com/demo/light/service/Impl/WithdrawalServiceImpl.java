package com.demo.light.service.Impl;

import com.demo.light.bean.DTO.WithdrawRequest;
import com.demo.light.bean.Wallet;
import com.demo.light.bean.Withdrawal;
import com.demo.light.enums.ChannelEnum;
import com.demo.light.enums.WithdrawalStatus;
import com.demo.light.repository.WalletMapper;
import com.demo.light.repository.WithdrawalMapper;
import com.demo.light.service.AlipayTransferService;
import com.demo.light.service.WithdrawalService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {
    private static final Logger log= LoggerFactory.getLogger(WithdrawalService.class);

    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private WithdrawalMapper withdrawalMapper;
    @Autowired
    private AlipayTransferService alipayTransferService;

    @Transactional
    @Override
    public void createWithdrawal(Long userId, WithdrawRequest request) {
        Wallet wallet= walletMapper.selectByUserId(userId);
        if (wallet.getBalance().compareTo(request.getAmount())<0){
            throw new RuntimeException("余额不足");
        }

        // ✅ 生成唯一的商户订单号（outBizNo）
        String outBizNo = "WD_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                "_" + userId +
                "_" + RandomStringUtils.randomNumeric(6);

        //创建提现记录（状态PENDING）
        //这个txId在什么时候生成呢？不在这一步生成
        Withdrawal record=Withdrawal.builder()
                .userId(userId)
                .amount(request.getAmount())
                .status(WithdrawalStatus.PENDING)
                .channelEnum(ChannelEnum.valueOf(request.getChannel()))
                .accountNo(request.getAccountNo())
                .accountName(request.getAccountName())
                .createdAt(LocalDateTime.now())
                .remark(request.getRemark())
                .outBizNo(outBizNo)
                .build();
        //插入record
        withdrawalMapper.insertRecord(record);
        walletMapper.freezeBalance(userId,request.getAmount());
    }
    @Scheduled(fixedRate = 60000)
    @Override
    public void processWithdrawals() {
        List<Withdrawal> pendingList=withdrawalMapper.findPending();

        if (pendingList == null||pendingList.isEmpty()) {
            return;
        }

        for (Withdrawal withdrawal: pendingList){
            //走的是支付宝
        try{
            //调用支付宝转账（传入outBizNo，保证幂等性）
            String txId=alipayTransferService.transfer(
                    withdrawal.getAccountNo(),
                    withdrawal.getAmount(),
                    withdrawal.getOutBizNo(),
                    withdrawal.getRemark()
            );

            //支付宝调用成功
            withdrawal.setTxId(txId);
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
            withdrawal.setProcessedAt(LocalDateTime.now());
            withdrawalMapper.updateRecordById(withdrawal);

            //清零冻结金额
            walletMapper.deductFrozenBalance(withdrawal.getUserId(),withdrawal.getAmount());

        }catch (Exception e){
            withdrawal.setStatus(WithdrawalStatus.FAILED);
            //为什么还要再记录一次时间呢？
            withdrawal.setProcessedAt(LocalDateTime.now());
            withdrawalMapper.updateRecordById(withdrawal);

            try{
                walletMapper.unfreezeBalance(withdrawal.getUserId(), withdrawal.getAmount());
            }catch (Exception error){
                log.error("解冻失败（提现已失败但无法解冻），用户ID: {}", withdrawal.getUserId(), error);
            }

        }
        }
    }
}
