package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getUserAuthKey;

import com.myproject.elearning.dto.projection.UserAuth;
import java.util.Random;
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
public class RedisAuthService {
    static final long DEFAULT_CACHE_DURATION = 3600; // 60 min
    static final long MAX_RANDOM_EXPIRY = 300; // 5 min
    static final long DEFAULT_CACHE_MISS_DURATION = 30;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    Random random;

    public UserAuth getCachedUser(String username) {
        return (UserAuth) valueOps.get(getUserAuthKey(username));
    }

    public void setCachedUser(String username, Object userAuthDTO) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getUserAuthKey(username), userAuthDTO, randomExpiry, TimeUnit.SECONDS);
    }

    public void setEmptyCache(String username) {
        valueOps.set(getUserAuthKey(username), "empty", DEFAULT_CACHE_MISS_DURATION, TimeUnit.SECONDS);
    }

    public void invalidateUserCache(String username) {
        redisTemplate.delete(getUserAuthKey(username));
    }
}
