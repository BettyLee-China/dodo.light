package com.demo.light.service.Impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniConsultResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.demo.light.service.AlipayTransferService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@Slf4j
public class AlipayTransferServiceImpl implements AlipayTransferService {

    @Autowired
    private AlipayClient alipayClient;
    /**
     * 单笔转账到支付宝账户
     * @param account 支付宝账号（手机号/邮箱）
     * @param amount 金额（单位：元，如 "100.00"）
     * @param outBizNo 商户唯一订单号（即你的 txId）
     * @param remark 备注
     * @return 支付宝交易号（trade_no），失败抛异常
     */
    @Override
    public String transfer(String account, BigDecimal amount, String outBizNo, String remark) {
        try{
            AlipayFundTransUniTransferRequest request=new AlipayFundTransUniTransferRequest();

            // 2. 构建业务参数（JSON 字符串）
            String bizContent = "{" +
                    "\"out_biz_no\":\"" + outBizNo + "\"," +
                    "\"trans_amount\":\"" + amount.toString() + "\"," +
                    "\"product_code\":\"TRANS_ACCOUNT_NO_PWD\"," +
                    "\"biz_scene\":\"DIRECT_TRANSFER\"," +
                    "\"payee_info\":{" +
                    "\"identity\":\"" + account + "\"," +
                    "\"identity_type\":\"ALIPAY_LOGON_ID\"" +
                    // 可选: + ",\"name\":\"张三\""
                    "}," +
                    "\"remark\":\"" + (remark != null ? remark : "") + "\"" +
                    "}";

            request.setBizContent(bizContent);

            //执行请求
            AlipayFundTransUniTransferResponse response=alipayClient.execute(request);

            //处理响应
            if (response.isSuccess()){
                log.info("支付宝转账受理成功，outBizNo={}",outBizNo);
            }else {
                log.error("支付宝转账失败, outBizNo={}, code={}, msg={}, subCode={}, subMsg={}",
                        outBizNo, response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg());
                throw new RuntimeException("提现失败: " + response.getSubMsg());
            }

        }catch (Exception e){
            log.error("调用支付宝转账异常",e);
            throw new RuntimeException("系统繁忙，请稍后重试",e);
        }
        return null;
    }
}
