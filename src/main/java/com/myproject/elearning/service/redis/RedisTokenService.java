package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getBlacklistKey;

import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisTokenService {
    static final long DEFAULT_CACHE_DURATION = 7 * 24 * 3600;
    static final long MAX_RANDOM_EXPIRY = 600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;

    public RedisTokenService(RedisTemplate<String, Object> redisTemplate, ValueOperations<String, Object> valueOps) {
        this.redisTemplate = redisTemplate;
        this.valueOps = valueOps;
    }

    public void revokeToken(String jti, Instant expireTime) {
        Duration ttl = Duration.between(Instant.now(), expireTime);
        valueOps.set(getBlacklistKey(jti), 1, ttl);
    }

    public Boolean isTokenRevoked(String jti) {
        return redisTemplate.hasKey(getBlacklistKey(jti));
    }
}
