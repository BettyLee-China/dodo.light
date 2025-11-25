package com.demo.light.service.Impl;


import com.demo.light.service.TokenBlacklistService;
import com.demo.light.utils.JwtUtil;
import com.demo.light.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private JwtUtil jwtUtil;
    private static final String BLACKLIST_PREFIX="blacklist:";


    @Override
    public void addToBlacklist(String token,long expirationTime) {
        redisUtil.addWithTimestampScore(BLACKLIST_PREFIX,token,expirationTime);
    }


public boolean isTokenBlacklisted(String token) {
    if (token == null || token.isEmpty()) {
        return false; // 防止空值传入 Redis
    }

    String key = BLACKLIST_PREFIX; // 假设你已经定义好了这个前缀
    Double score = redisUtil.getScore(key, token);

    // 如果 score 为 null，说明不存在于黑名单，返回 false
    return score != null && score > System.currentTimeMillis();
}
    @Override
    public void cleanExpiredTokens() {
        redisUtil.removeExpired(BLACKLIST_PREFIX);
    }
}
