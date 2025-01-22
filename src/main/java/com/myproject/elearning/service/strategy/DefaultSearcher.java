package com.myproject.elearning.service.strategy;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DefaultSearcher implements CourseSearcher {
    CourseRepository courseRepository;

    @Override
    public Page<Course> search(CourseFilters filters, PageRequest request) {
        // extract filters
        // log ...
        return courseRepository.findAllWithTopic(request);
    }
}
