package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRes {
    Long id;
    String title;
    String description;
    BigDecimal price;
    Course.CourseCategory category;
}
