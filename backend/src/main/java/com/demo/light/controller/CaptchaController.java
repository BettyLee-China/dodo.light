package com.demo.light.controller;

import com.demo.light.result.R;
import com.demo.light.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaptchaController {
    @Autowired
    CaptchaUtil captchaUtil;
    @GetMapping("/captcha")
    public R<Object> getCaptcha(String uuid){
        String captcha = captchaUtil.generateBase64Captcha(uuid);

        return R.builder().code(200).data(captcha).msg("获取验证码成功").build();
    }
}
