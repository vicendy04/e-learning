package com.myproject.elearning.rest.utils;

import com.myproject.elearning.dto.search.CourseFilters;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public final class PageBuilder {
    private PageBuilder() {}

    public static PageRequest of(CourseFilters filters, Sort sort) {
        return PageRequest.of(filters.getPage(), filters.getPageSize(), sort);
    }
}
