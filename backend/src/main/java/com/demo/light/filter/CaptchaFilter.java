package com.demo.light.filter;




import com.demo.light.utils.CaptchaUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private CaptchaUtil captchaUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            String requestURI = request.getRequestURI();
        if (requestURI.equals("/login")&&"POST".equals(request.getMethod())) {
            //拿captcha和uuid，存放在request的header中
            String uuid =request.getHeader("uuid");
            String captcha =request.getHeader("captcha");
                if (uuid == null||captcha==null||!captchaUtil.validateCaptcha(uuid,captcha)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":400,\"message\":\"验证码错误\"}");
                    return;
                }
                filterChain.doFilter(request,response);
                return;
        }

        filterChain.doFilter(request,response);
}
}
