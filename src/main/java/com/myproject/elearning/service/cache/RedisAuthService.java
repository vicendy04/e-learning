package com.myproject.elearning.service.cache;

import com.myproject.elearning.dto.projection.UserAuthDTO;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisAuthService {
    static String USER_AUTH_CACHE_KEY = "auth:user:";
    static long DEFAULT_CACHE_DURATION = 3600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;

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
