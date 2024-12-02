package com.myproject.elearning.service.cache;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisAuthService {
    static final String USER_AUTH_CACHE_KEY = "auth:user:";
    static final long DEFAULT_CACHE_DURATION = 3600;
    static final long DEFAULT_CACHE_MISS_DURATION = 30;
    static final long MAX_RANDOM_EXPIRY = 600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    Random random;

    public RedisAuthService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.random = new Random();
    }

    private String getUserKey(String username) {
        return USER_AUTH_CACHE_KEY + username;
    }

    public Object getCachedUser(String username) {
        return valueOps.get(getUserKey(username));
    }

    public void setCachedUser(String username, Object userAuthDTO) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getUserKey(username), userAuthDTO, randomExpiry, TimeUnit.SECONDS);
    }

    public void setEmptyCache(String username) {
        valueOps.set(getUserKey(username), "empty", DEFAULT_CACHE_MISS_DURATION, TimeUnit.SECONDS);
    }

    public void invalidateUserCache(String username) {
        redisTemplate.delete(getUserKey(username));
    }
}
