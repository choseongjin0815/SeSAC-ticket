package com.onspring.onspring_customer.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String id, String refreshToken) {
        String key = String.format("refresh_token:%s", id);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(7));
    }

    public String getRefreshToken(String id) {
        return redisTemplate.opsForValue().get("refresh_token:%s" + id);
    }

    public void deleteRefreshToken(String id) {
        redisTemplate.delete(String.format("refresh_token:%s", id));
    }
}
