package com.myproject.elearning.service.strategy;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PersonalizedSearcher implements CourseSearcher {
    UserRepository userRepository;
    CourseRepository courseRepository;

    //    bad performance
    //    Specification<Course> spec = filterCourses(filters);
    //    return courseRepository.findAll(spec,pageRequest);
    @Override
    public Page<Course> search(CourseFilters filters, PageRequest request) {
        // apply special offers
        // merge user preferences with provided filters
        // log ...

        var userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        Set<Long> ids = userRepository.getMyPreferencesIds(userId);

        Page<Long> courseIds = courseRepository.findIdsByTopicIds(ids, request);
        List<Course> courses = courseRepository.findAllWithTopicBy(courseIds.getContent());

        return new PageImpl<>(courses, courseIds.getPageable(), courseIds.getTotalElements());
    }
}
