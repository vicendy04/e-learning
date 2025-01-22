package com.myproject.elearning.dto.search;

import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseFilters {
    Set<Long> topicIds;
    Integer page;
    Integer pageSize;
}
