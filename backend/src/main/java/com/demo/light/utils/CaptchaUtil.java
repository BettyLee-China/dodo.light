package com.demo.light.utils;

import cn.hutool.captcha.ShearCaptcha;
import com.demo.light.enums.UserTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaptchaUtil {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JwtUtil jwtUtil;
    private static final int width = 200;
    private static  final  int height=80;
    private static final int thickness=2;

    public String generateBase64Captcha(String uuid){
        ShearCaptcha captcha = new ShearCaptcha(width,height,new MyCodeGenerator(),4);
        String code =captcha.getCode();
        String base64Image = "data:image/png;base64,"+captcha.getImageBase64();
        redisUtil.set("captcha:"+uuid,code,300);
        return base64Image;
    }

//    登录前的验证码操作
    public String login(String username, String uuid, String captcha, UserTypeEnum userType)throws Exception{
        String storedCaptcha=redisUtil.get("captcha:"+uuid);
        if (storedCaptcha == null||!storedCaptcha.equalsIgnoreCase(captcha)) {
            throw new Exception("验证码错误");
        }
        redisUtil.delete("captcha:"+uuid);
        return jwtUtil.createToken(username,userType);
    }
    public boolean validateCaptcha(String uuid,String captcha){
        String storedCaptcha=redisUtil.get("captcha:"+uuid);
        if (storedCaptcha == null||!storedCaptcha.equalsIgnoreCase(captcha)) {
            return false;
        }
        redisUtil.delete("captcha:"+uuid);
        return  true;
    }
}


