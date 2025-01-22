package com.myproject.elearning.service.test;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.myproject.elearning.repository.specification.CourseSpec.filterCourses;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PersonalizedCourseSearcher implements CourseSearcher {
    CourseRepository courseRepository;
    UserRepository userRepository;

    @Override
    public Page<Course> search(CourseFilters filters) {
        Long userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        Set<Long> ids = userRepository.getMyPreferencesIds(userId);
        // Merge user preferences with provided filters
        filters.getTopicIds().addAll(ids);

        Specification<Course> spec = filterCourses(filters);
        PageRequest pageRequest = PageRequest.of(
                filters.getPage(),
                filters.getPageSize(),
                Sort.by("id"));

        return courseRepository.findAll(spec, pageRequest);
    }

    private Specification<Course> createSpecification(CourseFilters filters) {
        // Implementation omitted - would create JPA specifications based on filters
        return null; // placeholder
    }
}
