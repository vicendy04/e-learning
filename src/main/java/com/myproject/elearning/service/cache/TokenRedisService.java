package com.myproject.elearning.service.cache;

import static com.myproject.elearning.constant.RedisKeyConstants.getBlacklistKey;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TokenRedisService {
    static final long DEFAULT_CACHE_DURATION = 900;

    ValueOperations<String, Object> valueOps;
    RedisTemplate<String, Object> redisTemplate;

    public void revokeToken(String jti, Instant expireTime) {
        Duration ttl = Duration.between(Instant.now(), expireTime);
        long effectiveTtl = Math.min(ttl.getSeconds(), DEFAULT_CACHE_DURATION);
        valueOps.set(getBlacklistKey(jti), 1, effectiveTtl, TimeUnit.SECONDS);
    }

    public Boolean isTokenRevoked(String jti) {
        return redisTemplate.hasKey(getBlacklistKey(jti));
    }
}
