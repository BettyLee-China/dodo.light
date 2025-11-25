package com.demo.light.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderEnum {
    MALE("男"),
    FEMALE("女");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static GenderEnum fromValue(String value) {
        for (GenderEnum gender : values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("未知 Gender: " + value);
    }
}