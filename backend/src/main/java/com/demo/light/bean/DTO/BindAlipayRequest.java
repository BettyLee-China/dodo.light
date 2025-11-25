package com.demo.light.bean.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class BindAlipayRequest {
    private String authCode;
    private String state;

}
