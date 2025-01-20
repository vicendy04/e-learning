package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRes {
    Long id;
    String title;
    String description;
    BigDecimal price;
    Course.Level level;
}
