package com.myproject.elearning.dto.search;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchCriteria {
    String query;
    String category;
    String level;
    Integer minDuration;
    Integer maxDuration;
    Double minPrice;
    Double maxPrice;
}
