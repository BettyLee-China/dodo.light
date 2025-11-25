package com.demo.light.service.Impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.demo.light.bean.Wallet;
import com.demo.light.bean.Withdrawal;
import com.demo.light.enums.WithdrawalStatus;
import com.demo.light.repository.WalletMapper;
import com.demo.light.repository.WithdrawalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class WalletServiceImpl {
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private WithdrawalMapper withdrawalMapper;


    public void bindAlipayByAuthCode(Long userId, String authCode) {

        try{
            AlipaySystemOauthTokenRequest request=new AlipaySystemOauthTokenRequest();
            request.setCode(authCode);
            request.setGrantType("authorization_code");

            AlipaySystemOauthTokenResponse response=alipayClient.execute(request);

            if (!response.isSuccess()) {
                throw new RuntimeException("支付宝授权失败"+response.getSubMsg());
            }

            String alipayUserId=response.getUserId();

            Wallet wallet=walletMapper.selectByUserId(userId);
            if (wallet == null) {
                wallet=Wallet.builder()
                        .userId(userId)
                        .balance(BigDecimal.ZERO)
                        .createdAt(LocalDateTime.now())
                        .build();
                walletMapper.insertWallet(wallet);
            }

            walletMapper.updateAlipayUserId(userId,alipayUserId,LocalDateTime.now());

        }catch (AlipayApiException e){
            throw  new RuntimeException("调用支付宝接口异常",e);
        }
    }


    //提现时直接使用已存储的user_id

    public void withdrawToAlipay(Long userId, BigDecimal amount) {
        // 1. 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        if (amount.scale() > 2) {
            throw new RuntimeException("金额最多保留两位小数");
        }

        // 2. 查询钱包信息
        Wallet wallet = walletMapper.selectByUserId(userId);
        if (wallet == null || wallet.getAlipayUserId() == null) {
            throw new RuntimeException("请先绑定支付宝账户");
        }

        // 3. 检查余额是否足够（假设你的业务需要）
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("账户余额不足");
        }

        // 4. 生成唯一业务订单号（幂等性关键！）
        String outBizNo = "WD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 5. 创建提现记录（状态：处理中）
        Withdrawal record = new Withdrawal();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setAlipayUserId(wallet.getAlipayUserId());
        record.setOutBizNo(outBizNo);
        record.setStatus(WithdrawalStatus.PROCESSING); // 处理中
        record.setCreatedAt(LocalDateTime.now());
        withdrawalMapper.insertRecord(record);

        try {
            // 6. 调用支付宝转账接口
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();

            // 构建业务参数
            String bizContent = "{" +
                    "\"out_biz_no\":\"" + outBizNo + "\"," +
                    "\"trans_amount\":\"" + amount.setScale(2).toString() + "\"," +
                    "\"product_code\":\"TRANSFER_TO_ALIPAY_ACCOUNT\"," +
                    "\"biz_scene\":\"DIRECT_TRANSFER\"," +
                    "\"order_title\":\"用户提现\"," +
                    "\"payee_info\":{" +
                    "\"identity\":\"" + wallet.getAlipayUserId() + "\"," +
                    "\"identity_type\":\"ALIPAY_USER_ID\"," +
                    "\"name\":\"*\""  +
            "}" +
                    "}";

            request.setBizContent(bizContent);

            // 执行请求
            AlipayFundTransUniTransferResponse response = alipayClient.execute(request);

            // 7. 处理响应
            if (response.isSuccess()) {
                // 转账成功（注意：success 表示请求受理成功，不一定是到账！）
                record.setStatus(WithdrawalStatus.SUCCESS);
                record.setProcessedAt(LocalDateTime.now());
                record.setTxId(response.getOrderId()); // 支付宝流水号
                withdrawalMapper.updateRecordById(record);

                // 扣减用户余额
                walletMapper.deductBalance(amount,userId);

            } else {
                // 调用失败（如参数错误、风控拦截等）
                record.setStatus(WithdrawalStatus.FAILED);
                withdrawalMapper.updateRecordById(record);

                throw new RuntimeException("提现失败: " );
            }

        } catch (AlipayApiException e) {
            // 网络或签名等底层异常
            record.setStatus(WithdrawalStatus.FAILED);
            withdrawalMapper.updateRecordById(record);
            throw new RuntimeException("调用支付宝接口异常", e);
        }
    }

}

