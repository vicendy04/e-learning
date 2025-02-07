package com.myproject.elearning.service.cache;

import static com.myproject.elearning.constant.RedisKeyConstants.getUserAuthKey;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.service.UserService;
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
public class AuthRedisService {
    static final long DEFAULT_CACHE_DURATION = 5;

    UserService userService;
    ValueOperations<String, Object> valueOps;
    RedisTemplate<String, Object> redisTemplate;

    public UserAuth getAside(String email) {
        UserAuth data = this.get(email);
        if (data != null) return data;
        data = userService.findAuthDTOByEmail(email);
        this.set(email, data);
        return data;
    }

    private UserAuth get(String email) {
        return (UserAuth) valueOps.get(getUserAuthKey(email));
    }

    public void set(String username, Object userAuthDTO) {
        valueOps.set(getUserAuthKey(username), userAuthDTO, DEFAULT_CACHE_DURATION, TimeUnit.MINUTES);
    }

    public void invalidateUserCache(String username) {
        redisTemplate.delete(getUserAuthKey(username));
    }
}
