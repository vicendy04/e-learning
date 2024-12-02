package com.myproject.elearning.service.cache;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisBlackListService {
    static final String BLACKLIST_PREFIX = "token:blacklist:";
    static final long DEFAULT_CACHE_DURATION = 7 * 24 * 3600;
    static long MAX_RANDOM_EXPIRY = 600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;

    public RedisBlackListService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    private String getCourseKey(String jti) {
        return BLACKLIST_PREFIX + jti;
    }

    public void revokeToken(String jti, Instant expireTime) {
        // check expireTime to choose appropriate ttl
        //        Duration ttl = Duration.between(Instant.now(), expireTime);
        //        valueOps.set(key, "revoked", ttl);
        valueOps.set(getCourseKey(jti), "revoked", DEFAULT_CACHE_DURATION);
    }

    public Boolean isTokenRevoked(String jti) {
        return redisTemplate.hasKey(getCourseKey(jti));
    }
}
