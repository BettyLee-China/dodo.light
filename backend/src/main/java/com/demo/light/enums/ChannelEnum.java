package com.demo.light.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChannelEnum {
    BANK("BANK","银行"),
    ALIPAY("ALIPAY","支付宝"),
    WECHAT("WECHAT","微信");
    private final String channelName;
    private final String channelValue;

    @Override
    public String toString() {
        return  channelName;
    }
}
