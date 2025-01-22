package com.myproject.elearning.service.test;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.myproject.elearning.repository.specification.CourseSpec.filterCourses;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class DefaultCourseSearcher implements CourseSearcher {
    CourseRepository courseRepository;

    @Override
    public Page<Course> search(CourseFilters filters) {
        Specification<Course> spec = filterCourses(filters);
        PageRequest pageRequest = PageRequest.of(
                filters.getPage(),
                filters.getPageSize(),
                Sort.by("id")
        );
        return courseRepository.findAll(spec, pageRequest);
    }

    private Specification<Course> createSpecification(CourseFilters filters) {
        // Implementation omitted - would create JPA specifications based on filters
        return null; // placeholder
    }
}
