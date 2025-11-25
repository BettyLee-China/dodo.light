package com.demo.light.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey;
    private long expiration;
    private String tokenHeader;
    private String tokenPrefix;//token前缀，不知道有什么用。通过前缀来拿token
}
