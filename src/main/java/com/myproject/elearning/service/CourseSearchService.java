package com.myproject.elearning.service;

import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.dto.search.SearchCriteria;
import com.myproject.elearning.repository.CourseSearchRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseSearchService {
    CourseSearchRepository courseSearchRepository;

    public PagedRes<CourseDocument> searchCourses(SearchCriteria criteria, int page, int size) {
        try {
            return courseSearchRepository.search(criteria, page, size);
        } catch (Exception e) {
            throw new RuntimeException("Error searching courses", e);
        }
    }
}
