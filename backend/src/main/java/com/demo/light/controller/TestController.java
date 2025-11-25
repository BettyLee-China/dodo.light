package com.demo.light.controller;

import com.demo.light.bean.VO.PhotoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/test-photo-vo")
    public PhotoVO testPhotoVO() {
        PhotoVO vo = new PhotoVO();
        vo.setId(123L);
        vo.setPrice(new BigDecimal("88.88"));
        vo.setStock(100);
        return vo; // Spring MVC 会自动序列化为 JSON
    }
    @GetMapping("/test-auth")
    public String testAuth() {
        String state = "test123";
        redisTemplate.opsForValue().set("alipay_auth_state:" + state, "valid", 5, TimeUnit.MINUTES);
        return "Now visit: /alipay/callback?auth_code=abc&state=" + state;
    }
}
