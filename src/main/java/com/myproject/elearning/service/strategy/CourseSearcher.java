package com.myproject.elearning.service.strategy;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.search.CourseFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CourseSearcher {
    Page<CourseData> search(CourseFilters filters, PageRequest request);
}
