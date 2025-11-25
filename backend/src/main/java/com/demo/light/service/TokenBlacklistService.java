package com.demo.light.service;

public interface TokenBlacklistService {
    void addToBlacklist(String token,long expirationTime);
    boolean isTokenBlacklisted(String token);
    void cleanExpiredTokens();
}
