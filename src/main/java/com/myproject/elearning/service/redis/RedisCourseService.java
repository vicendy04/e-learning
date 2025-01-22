package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getCourseKey;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.service.CourseService;
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
public class RedisCourseService {
    static final long DEFAULT_CACHE_DURATION = 3600; // 60 min
    static final long MAX_RANDOM_EXPIRY = 300; // 5 min

    Random random;
    CourseService courseService;
    ValueOperations<String, Object> valueOps;
    RedisTemplate<String, Object> redisTemplate;

    public CourseData getAside(Long courseId) {
        CourseData data = this.get(courseId);
        if (data != null) return data;
        data = courseService.getCourse(courseId);
        this.set(courseId, data);
        return data;
    }

    public CourseData get(Long id) {
        Object obj = valueOps.get(getCourseKey(id));
        return obj != null ? (CourseData) obj : null;
    }

    public void set(Long id, CourseData course, long expiryTimeInSeconds) {
        valueOps.set(getCourseKey(id), course, expiryTimeInSeconds, TimeUnit.SECONDS);
    }

    public void set(Long id, CourseData course) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getCourseKey(id), course, randomExpiry, TimeUnit.SECONDS);
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

    public void invalidateCourseCache(Long id) {
        redisTemplate.delete(getCourseKey(id));
    }
}
