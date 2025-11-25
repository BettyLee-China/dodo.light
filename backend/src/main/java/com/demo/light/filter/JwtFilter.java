package com.demo.light.filter;


import com.demo.light.service.TokenBlacklistService;
import com.demo.light.utils.JwtUtil;
import com.demo.light.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    private final TokenBlacklistService tokenBlacklistService;

    private RedisUtil redisUtil;


    public JwtFilter(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService, RedisUtil redisUtil){
        this.jwtUtil=jwtUtil;
        this.tokenBlacklistService=tokenBlacklistService;
        this.redisUtil=redisUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println(">>>>>>>>>"+request.getHeader("Content-Type"));
        String token =jwtUtil.getTokenFromRequest(request);
        String path=request.getRequestURI();
        if (path.startsWith("/captcha")||path.startsWith("/login")){
            filterChain.doFilter(request,response);
            return;
        }

        if (token != null && jwtUtil.isValidateToken(token)&&!tokenBlacklistService.isTokenBlacklisted(token)) {
            String userId = jwtUtil.parseToken(token);
            Collection<? extends GrantedAuthority> authorities = Arrays.stream( redisUtil.get("token-auth",userId).split(","))
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());


            if (authorities != null) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request,response);
    }


}
