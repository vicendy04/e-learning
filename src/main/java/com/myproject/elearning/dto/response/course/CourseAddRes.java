package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseAddRes {
    Long id;
    String title;
    String description;
    Integer duration;
    BigDecimal price;
    Course.Level level;
    String thumbnailUrl;
    Integer enrolledCount;
    Integer reviewCount;
    Long instructorId;
    Long topicId;
}
