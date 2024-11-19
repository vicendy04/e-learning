package com.myproject.elearning.service.cache;

import com.myproject.elearning.dto.response.course.CourseGetResponse;
import com.myproject.elearning.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CourseCacheService {
    private final RedisService redisService;

    private static final String COURSE_CACHE_KEY = "course:";
    private static final long CACHE_DURATION = 3600;

    private String getCourseCacheKey(Long id) {
        return COURSE_CACHE_KEY + id;
    }

    public CourseGetResponse getCachedCourse(Long id) {
        return redisService.getEntity(getCourseCacheKey(id), CourseGetResponse.class);
    }

    public void setCachedCourse(Long id, CourseGetResponse course) {
        redisService.set(getCourseCacheKey(id), course, CACHE_DURATION);
    }

    public void invalidateCache(Long id) {
        redisService.del(getCourseCacheKey(id));
    }
}
