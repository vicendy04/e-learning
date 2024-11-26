package com.myproject.elearning.service.cache;

import com.myproject.elearning.dto.response.course.CourseGetResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCourseService {
    private static final String COURSE_CACHE_KEY = "course:";
    private static final long DEFAULT_CACHE_DURATION = 3600;
    private static final long MAX_RANDOM_EXPIRY = 600;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;

    public RedisCourseService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
    }

    private String getCourseKey(Long id) {
        return COURSE_CACHE_KEY + id;
    }

    public CourseGetResponse getCachedCourse(Long id) {
        Object obj = valueOps.get(getCourseKey(id));
        if (obj != null) {
            return (CourseGetResponse) obj;
        }
        return null;
    }

    public void setCachedCourse(Long id, CourseGetResponse course, long expiryTimeInSeconds) {
        valueOps.set(getCourseKey(id), course, expiryTimeInSeconds, TimeUnit.SECONDS);
    }

    public void setCachedCourse(Long id, CourseGetResponse course) {
        Random random = new Random();
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getCourseKey(id), course, randomExpiry, TimeUnit.SECONDS);
    }

    public void invalidateCache(Long id) {
        redisTemplate.delete(getCourseKey(id));
    }

    public void invalidateAllCourseCache() {
        Set<String> keys = redisTemplate.keys(COURSE_CACHE_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public Long getCacheExpiration(Long id) {
        return redisTemplate.getExpire(getCourseKey(id), TimeUnit.SECONDS);
    }

    public void extendCacheExpiration(Long id, long additionalTimeInSeconds) {
        Long currentExpiration = getCacheExpiration(id);
        if (currentExpiration != null && currentExpiration > 0) {
            redisTemplate.expire(getCourseKey(id), currentExpiration + additionalTimeInSeconds, TimeUnit.SECONDS);
        }
    }

    public Boolean isCachePresent(Long id) {
        return redisTemplate.hasKey(getCourseKey(id));
    }
}
