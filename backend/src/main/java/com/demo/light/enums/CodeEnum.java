package com.demo.light.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public enum CodeEnum {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    INVALID_CAPTCHA(400, "验证码错误"),
    NOT_FOUND(404, "资源未找到"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "没有权限访问"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INVALID_USERNAME_OR_PASSWAORD(400, "用户名或密码错误"),
    AUTHENTICATION_FAILED(400, "认证失败"),
    LOGIN_EXCEPTION(400, "登录异常"),
    USERNAME_NOT_FOUND(400,"用户名不存在"),

    REFUND_FAIL(400,"退款失败"),

    INSERT_FAIL(400,"注册失败");

    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private String msg;
}
