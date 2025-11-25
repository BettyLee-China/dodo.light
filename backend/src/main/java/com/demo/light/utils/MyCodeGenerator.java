package com.demo.light.utils;

import cn.hutool.captcha.generator.CodeGenerator;

import java.util.Random;


public class MyCodeGenerator implements CodeGenerator {
    @Override
    public String generate() {
        Random random=new Random();
       int code= random.nextInt(9000)+1000;
        return String.valueOf(code);
    }

    @Override
    public boolean verify(String s, String s1) {

      return  s.equalsIgnoreCase(s1);
    }
}
