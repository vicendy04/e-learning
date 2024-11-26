package com.myproject.elearning.service.cache;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.auth.UserAuthDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisAuthService {
    private static final String USER_AUTH_CACHE_KEY = "auth:user:";
    private static final long DEFAULT_CACHE_DURATION = 3600;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public RedisAuthService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    private String getUserKey(String username) {
        return USER_AUTH_CACHE_KEY + username;
    }

    public UserAuthDTO getCachedUser(String username) {
        Object obj = valueOps.get(getUserKey(username));
        if (obj != null) {
            return (UserAuthDTO) obj;
        }
        return null;
    }

    public void setCachedUser(String username, UserAuthDTO userAuthDTO) {
        valueOps.set(getUserKey(username), userAuthDTO, DEFAULT_CACHE_DURATION, TimeUnit.SECONDS);
    }

    public void invalidateUserCache(String username) {
        redisTemplate.delete(getUserKey(username));
    }
}