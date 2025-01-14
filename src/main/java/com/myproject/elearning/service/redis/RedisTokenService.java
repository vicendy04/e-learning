package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getBlacklistKey;

import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisTokenService {
    static final long DEFAULT_CACHE_DURATION = 24 * 3600; // 1 day
    static final long MAX_RANDOM_EXPIRY = 300; // 5 min

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;

    public void revokeToken(String jti, Instant expireTime) {
        Duration ttl = Duration.between(Instant.now(), expireTime);
        long effectiveTtl = Math.min(ttl.getSeconds(), DEFAULT_CACHE_DURATION);
        valueOps.set(getBlacklistKey(jti), 1, effectiveTtl);
    }

    public Boolean isTokenRevoked(String jti) {
        return redisTemplate.hasKey(getBlacklistKey(jti));
    }
}
