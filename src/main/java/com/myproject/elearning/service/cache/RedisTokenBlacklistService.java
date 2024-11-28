package com.myproject.elearning.service.cache;

import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisTokenBlacklistService {
    static String BLACKLIST_PREFIX = "token:blacklist:";
    static long DEFAULT_CACHE_DURATION = 3600;
    static long MAX_RANDOM_EXPIRY = 600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;

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
