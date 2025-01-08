package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseGetRes {
    Long id;
    String title;
    String description;
    int duration;
    BigDecimal price;
    Course.CourseCategory category;
    int enrolledCount;
    Course.Level level;
}
