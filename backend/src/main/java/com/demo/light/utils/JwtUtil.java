package com.demo.light.utils;

import com.demo.light.bean.User;
import com.demo.light.config.JwtProperties;
import com.demo.light.enums.UserTypeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
    // 获取签名密钥
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    // 生成 JWT Token
    public String createToken(String userId,UserTypeEnum userType) {
        return Jwts.builder()
                .subject(userId)
                .claim("userType",userType.getValue())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignInKey()) // 使用 SecretKey 签名
                .compact();
    }

    // 验证 Token 是否有效
    public boolean isValidateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 解析 Token 获取用户ID
    public String parseToken(String token) {
        byte[] key= jwtProperties.getSecretKey().getBytes();
        SecretKey secretKey=Keys.hmacShaKeyFor(key);


        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)           // ✅ 正确：传入 Key
                    .build()
                    .parseSignedClaims(token);            // 返回 JWS
            return jws.getPayload().getSubject();         // 获取 subject
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Token 已过期", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Token 格式错误", e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Token 签名无效", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    // 从请求头中提取 Token
    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(jwtProperties.getTokenHeader()); // 如 "Authorization"
        if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenPrefix() + " ")) {
            return authHeader.substring(jwtProperties.getTokenPrefix().length() + 1);
        }
        return null;
    }
}