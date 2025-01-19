package com.myproject.elearning.service;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.service.database.CourseDBService;
import com.myproject.elearning.service.redis.RedisCourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseService {
    CourseDBService courseDBService;
    RedisCourseService redisCourseService;

    /**
     * Retrieves course data from cache, falling back to the database if not found.
     */
    public CourseData getCourse(Long courseId) {
        CourseData data = redisCourseService.get(courseId);
        if (data != null) return data;
        data = courseDBService.getDBCourse(courseId);
        redisCourseService.set(courseId, data);
        return data;
    }
}
