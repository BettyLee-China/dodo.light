package com.demo.light.service;

import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.demo.light.result.R;
import org.springframework.stereotype.Service;

@Service
public interface RefundService {
    R<String> createRefund(AlipayTradeRefundModel refundModel);

    R<String> processRefund(String outRequestNo);
}
