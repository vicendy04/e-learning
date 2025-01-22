package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getCourseKey;

import com.myproject.elearning.constant.RedisKeyConstants;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.service.CourseService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseRedisService {
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

    public List<CourseData> getCourses(List<Long> ids) {
        Set<String> cacheKeys =
                ids.stream().map(RedisKeyConstants::getCourseKey).collect(Collectors.toSet());
        List<Object> cacheValues = valueOps.multiGet(cacheKeys);
        if (cacheValues != null) {
            return cacheValues.stream()
                    .filter(Objects::nonNull)
                    .map(o -> (CourseData) o)
                    .toList();
        }
        return Collections.emptyList();
    }

    public void setCourses(List<CourseData> courses) {
        Map<String, CourseData> cacheMap = new HashMap<>();
        for (CourseData course : courses) {
            cacheMap.put(RedisKeyConstants.getCourseKey(course.getId()), course);
        }
        valueOps.multiSet(cacheMap);
        cacheMap.keySet().forEach(key -> {
            long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
            redisTemplate.expire(key, randomExpiry, TimeUnit.SECONDS);
        });
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
