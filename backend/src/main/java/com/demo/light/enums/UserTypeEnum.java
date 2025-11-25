package com.demo.light.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum UserTypeEnum {
    Customer("customer","客户"),
    Photographer("photographer","摄影师");
    private final String value;
    private final String desc;

    UserTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // 用于 JSON 序列化（如放入 JWT 或返回给前端）
    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 静态方法：根据 value 查找枚举（解析 Token 时用）
    public static UserTypeEnum fromValue(String value) {
        for (UserTypeEnum type : UserTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的 UserTypeEnum: " + value);
    }

    // 可选：支持忽略大小写的解析
    public static UserTypeEnum fromValueIgnoreCase(String value) {
        if (value == null) return null;
        return fromValue(value.trim().toLowerCase());
    }

}
