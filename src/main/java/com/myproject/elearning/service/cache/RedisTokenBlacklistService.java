package com.myproject.elearning.service.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class RedisTokenBlacklistService {
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final long DEFAULT_CACHE_DURATION = 3600;
    private static final long MAX_RANDOM_EXPIRY = 600;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public RedisTokenBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }


    public void revokeToken(String jti, Instant expireTime) {
        String key = BLACKLIST_PREFIX + jti;
        Duration ttl = Duration.between(Instant.now(), expireTime);
        valueOps.set(key, "revoked", ttl);
    }

    public Boolean isTokenRevoked(String jti) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + jti);
    }
}
