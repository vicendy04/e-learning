package com.myproject.elearning.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    public Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }

    public void delByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public <T> T getEntity(String key, Class<T> entityClass) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            return entityClass.cast(obj);
        }
        return null;
    }
}
