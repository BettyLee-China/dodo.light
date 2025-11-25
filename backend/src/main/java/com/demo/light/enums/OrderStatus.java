package com.demo.light.enums;

public enum OrderStatus {
    PENDING_PAYMENT("待支付"),
    PAID("已支付"),
    CONFIRMED("已确认"),
    DELIVERING("配送中"),
    DELIVERED("已送达"),
    COMPLETED("已完成"),
    CANCELLED("已取消"),
    CLOSED("已关闭");

    private final String desc;
    OrderStatus(String desc) { this.desc = desc; }
}
