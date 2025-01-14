package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.COURSE_PREFIX;
import static com.myproject.elearning.constant.RedisKeyConstants.getCourseKey;

import com.myproject.elearning.dto.response.course.CourseGetRes;
import java.util.Random;
import java.util.Set;
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

    RedisTemplate<String, Object> redisTemplate;
    ValueOperations<String, Object> valueOps;
    Random random;

    public CourseGetRes getCachedCourse(Long id) {
        Object obj = valueOps.get(getCourseKey(id));
        if (obj != null) {
            return (CourseGetRes) obj;
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

    public void invalidateAllCourseCache() {
        Set<String> keys = redisTemplate.keys(COURSE_PREFIX + "*");
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

    public void invalidateCourseCache(Long id) {
        redisTemplate.delete(getCourseKey(id));
    }
}
