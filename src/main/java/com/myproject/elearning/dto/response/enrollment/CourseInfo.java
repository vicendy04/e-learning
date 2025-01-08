package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseInfo {
    Long id;
    String title;
    String description;
    BigDecimal price;
    Course.CourseCategory category;
}
