package com.demo.light.bean.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String birthday;
    private String role;

    //摄影师专属字段
    private String realName;
    private String idCardNumber;
    private MultipartFile idCardFront;
    private MultipartFile idCardBack;


}
