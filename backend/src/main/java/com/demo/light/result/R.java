package com.demo.light.result;


import com.demo.light.enums.CodeEnum;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> OK(T data){
        R<T> build = R.<T>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMsg())
                .data(data)
                .build();
        return build;
    }
    public static <T> R<T> OK(){
        return R.<T>builder()
                .code(CodeEnum.SUCCESS.getCode())
                .msg(CodeEnum.SUCCESS.getMsg())
                .build();
    }
    public static <T> R<T> FAIL(CodeEnum codeEnum){
        return R.<T>builder()
                .code(codeEnum.getCode())
                .msg(codeEnum.getMsg())
                .build();
    }
}
