package com.demo.light.service.Impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.demo.light.bean.Refund;
import com.demo.light.enums.CodeEnum;
import com.demo.light.repository.RefundMapper;
import com.demo.light.result.R;
import com.demo.light.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RefundServiceImpl implements RefundService {
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private AlipayClient alipayClient;

    //创建退款记录（在数据库中生成数据）
    @Override
    public R<String> createRefund(AlipayTradeRefundModel refundModel) {

        //1.检查是否已存在相同 outRequestNo的退款
        Refund existing =refundMapper.selectByOutRequestNo(refundModel.getOutRequestNo());
        if (existing != null) {
            return R.OK("退款申请已存在"+existing.getRefundNo());
        }


        //2.创建退款记录（PENDING）这是没有存在记录的情况

        Refund refund=Refund.builder()
                .outRequestNo(refundModel.getOutRequestNo())
                .orderId( refundModel.getOutTradeNo())
                .refundAmount(new BigDecimal(refundModel.getRefundAmount()))
                .reason(refundModel.getRefundReason())
                .status("PENDING")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        //插入数据库
        int row=refundMapper.insertRefund(refund);
        if (row <= 0) {
            return R.FAIL(CodeEnum.BAD_REQUEST);
        }

        return R.OK("创建退款订单成功");
    }
    //真正的退款过程

    @Override
    public R<String> processRefund(String outRequestNo) {
        //从数据库查找到退款记录
        Refund refund=refundMapper.selectByOutRequestNo(outRequestNo);
        if (refund == null) {
            return R.FAIL(CodeEnum.BAD_REQUEST);
        }
        if (!"PENDING".equals(refund.getStatus())) {
            return R.OK("退款已处理，当前状态"+refund.getStatus());
        }

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setOutTradeNo(refund.getOrderId());
        model.setOutRequestNo(refund.getOutRequestNo());
        model.setRefundAmount(refund.getRefundAmount().toString());
        model.setRefundReason(refund.getReason());

        AlipayTradeRefundRequest request=new AlipayTradeRefundRequest();
        request.setBizModel(model);

        try {
            AlipayTradeRefundResponse response=alipayClient.execute(request);
            if (response.isSuccess()){
                refundMapper.updateStatus(outRequestNo,"SUCCESS");
                return R.OK("退款成功");
            }else {
                refundMapper.updateStatus(outRequestNo,"FAILED");
                return R.FAIL(CodeEnum.BAD_REQUEST);
            }
        } catch (AlipayApiException e) {
            refundMapper.updateStatus(outRequestNo,"FAILED");
            throw new RuntimeException(e);
        }


    }
}
