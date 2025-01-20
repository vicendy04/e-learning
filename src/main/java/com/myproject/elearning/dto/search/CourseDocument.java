package com.myproject.elearning.dto.search;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Todo: link
public class CourseDocument {
    String id;
    String title;
    String description;
    String category;
    String level;
    Integer duration;
    Double price;
    String thumbnailUrl;

    //    Integer enrolledCount;
    //    Integer reviewCount;

    //    Integer totalLessons;

    String instructorId;
    String instructorName;
    String imageUrl;
}
