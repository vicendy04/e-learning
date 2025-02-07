package com.myproject.elearning.service.strategy;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.service.cache.CourseRedisService;
import java.util.List;
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
public class DefaultSearcher implements CourseSearcher {
    CourseRepository courseRepository;
    CourseRedisService courseRedisService;

    @Override
    public Page<CourseData> search(CourseFilters filters, PageRequest page) {
        // extract filters
        // log ...

        Page<Long> courseIds = courseRepository.findIdsBy(page);

        // read cache, set aside
        List<CourseData> data = courseRedisService.getManyAside(courseIds.getContent());

        return new PageImpl<>(data, courseIds.getPageable(), courseIds.getTotalElements());
    }
}
