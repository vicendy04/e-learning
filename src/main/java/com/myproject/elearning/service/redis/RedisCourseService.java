package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.*;

import com.myproject.elearning.dto.response.course.CourseGetRes;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisCourseService {
    static final long DEFAULT_CACHE_DURATION = 3600;
    static final long MAX_RANDOM_EXPIRY = 600;

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    Random random;

    public RedisCourseService(RedisTemplate<String, Object> redisTemplate, ValueOperations<String, Object> valueOps) {
        this.redisTemplate = redisTemplate;
        this.valueOps = valueOps;
        this.random = new Random();
    }

    public CourseGetRes getCachedCourse(Long id) {
        Object obj = valueOps.get(getCourseKey(id));
        if (obj != null) {
            return (CourseGetRes) obj;
        }
        return null;
    }

    public Integer getCachedEnrollmentCount(Long id) {
        Object obj = valueOps.get(getEnrollmentCountKey(id));
        if (obj != null) {
            return (Integer) obj;
        }
        return null;
    }

    public void setCachedCourse(Long id, CourseGetRes course, long expiryTimeInSeconds) {
        valueOps.set(getCourseKey(id), course, expiryTimeInSeconds, TimeUnit.SECONDS);
    }

    public void setCachedCourse(Long id, CourseGetRes course) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getCourseKey(id), course, randomExpiry, TimeUnit.SECONDS);
    }

    public void setCachedEnrollmentCount(Long id, Integer count) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getEnrollmentCountKey(id), count, randomExpiry, TimeUnit.SECONDS);
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

    public void incrementEnrollmentCount(Long courseId) {
        String key = getEnrollmentCountKey(courseId);
        valueOps.increment(key);
    }

    public void decrementEnrollmentCount(Long courseId) {
        String key = getEnrollmentCountKey(courseId);
        valueOps.decrement(key);
    }
}
