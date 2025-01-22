package com.myproject.elearning.service.strategy;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.search.CourseFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CourseSearcher {
    Page<Course> search(CourseFilters filters, PageRequest request);
}
