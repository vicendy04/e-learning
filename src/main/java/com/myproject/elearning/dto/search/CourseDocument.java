package com.myproject.elearning.dto.search;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDocument {
    String id;
    String title;
    String description;
    String category;
    String level;
    Integer duration;
    Double price;
    String instructorName;
}
