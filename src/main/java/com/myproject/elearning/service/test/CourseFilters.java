package com.myproject.elearning.service.test;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CourseFilters {
    private String searchTerm;
    private Set<Long> topicIds;
    private String level;
    private Integer page;
    private Integer pageSize;
    // getters, setters, constructors omitted for brevity
}
