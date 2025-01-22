package com.myproject.elearning.service.test;

import com.myproject.elearning.domain.Course;
import org.springframework.data.domain.Page;

public interface CourseSearcher {
    Page<Course> search(CourseFilters filters);
}