package com.myproject.elearning.service.strategy;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.CourseRedisService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    CourseRedisService courseRedisService;

    //    bad performance
    //    Specification<Course> spec = filterCourses(filters);
    //    return courseRepository.findAll(spec,pageRequest);
    @Override
    public Page<CourseData> search(CourseFilters filters, PageRequest request) {
        // apply special offers
        // merge user preferences with provided filters
        // log ...

        // custom logic
        var userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        Set<Long> ids = userRepository.getMyPreferencesIds(userId);

        // find course ids based on user interests
        Page<Long> courseIds = courseRepository.findIdsByTopicIds(ids, request);

        // read cache
        List<CourseData> inCache = courseRedisService.getCourses(courseIds.getContent());
        List<Long> idsNotInCache = this.filterIds(courseIds.getContent(), inCache);
        List<CourseData> inDB;
        if (idsNotInCache.isEmpty()) {
            inDB = Collections.emptyList();
        } else {
            inDB = courseRepository.findAllWithTopicBy(idsNotInCache);
            courseRedisService.setCourses(inDB);
        }

        var combinedData = Stream.concat(inCache.stream(), inDB.stream()).toList();
        return new PageImpl<>(combinedData, courseIds.getPageable(), courseIds.getTotalElements());
    }

    private List<Long> filterIds(List<Long> courseIds, List<CourseData> inCache) {
        List<Long> idsInCache = inCache.stream().map(CourseData::getId).toList();
        return courseIds.stream().filter(id -> !idsInCache.contains(id)).collect(Collectors.toList());
    }
}
