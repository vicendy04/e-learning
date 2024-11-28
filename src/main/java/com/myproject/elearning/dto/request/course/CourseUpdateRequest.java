package com.myproject.elearning.dto.request.course;

import com.myproject.elearning.domain.Course;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {
    String description;

    @PositiveOrZero
    int duration;

    @PositiveOrZero
    BigDecimal price;

    Course.CourseCategory category;
}
