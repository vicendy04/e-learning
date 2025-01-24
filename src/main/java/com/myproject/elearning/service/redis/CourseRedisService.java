package com.myproject.elearning.service.redis;

import static com.myproject.elearning.constant.RedisKeyConstants.getCourseKey;

import com.myproject.elearning.constant.RedisKeyConstants;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.service.CourseService;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    static final long DEFAULT_CACHE_DURATION = 30;
    static final long MAX_RANDOM_EXPIRY = 5;

    Random random;
    CourseService courseService;
    CourseRepository courseRepository;
    ValueOperations<String, Object> valueOps;
    RedisTemplate<String, Object> redisTemplate;

    public CourseData getAside(Long courseId) {
        CourseData data = this.get(courseId);
        if (data != null) return data;
        data = courseService.getCourse(courseId);
        this.set(courseId, data);
        return data;
    }

    public List<CourseData> getManyAside(List<Long> courseIds) {
        List<CourseData> inCache = this.getCourses(courseIds);
        List<Long> idsNotInCache = this.filterIds(courseIds, inCache);
        List<CourseData> inDB;
        if (idsNotInCache.isEmpty()) {
            inDB = Collections.emptyList();
        } else {
            inDB = courseRepository.findAllWithTopicBy(idsNotInCache);
            this.setCourses(inDB);
        }
        return Stream.concat(inCache.stream(), inDB.stream()).toList();
    }

    private List<Long> filterIds(List<Long> courseIds, List<CourseData> inCache) {
        List<Long> idsInCache = inCache.stream().map(CourseData::getId).toList();
        return courseIds.stream().filter(id -> !idsInCache.contains(id)).toList();
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
            redisTemplate.expire(key, randomExpiry, TimeUnit.MINUTES);
        });
    }

    public void set(Long id, CourseData course) {
        long randomExpiry = DEFAULT_CACHE_DURATION + random.nextInt((int) MAX_RANDOM_EXPIRY);
        valueOps.set(getCourseKey(id), course, randomExpiry, TimeUnit.MINUTES);
    }

    public boolean isCachePresent(Long id) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getCourseKey(id)));
    }

    public void invalidateCourseCache(Long id) {
        redisTemplate.delete(getCourseKey(id));
    }
}
