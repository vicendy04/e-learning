package com.myproject.elearning.service;

import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.dto.search.SearchCriteria;
import com.myproject.elearning.repository.CourseSearchRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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

    public void indexCourse(CourseGetRes course) {
        try {
            courseSearchRepository.indexCourse(course);
        } catch (Exception e) {
            throw new RuntimeException("Error indexing course", e);
        }
    }

    public void setupMeilisearch() {
        try {
            courseSearchRepository.setupMeilisearch();
        } catch (Exception e) {
            log.error("Failed to index courses: ", e);
            throw new RuntimeException("Error indexing all courses: " + e.getMessage(), e);
        }
    }
}
