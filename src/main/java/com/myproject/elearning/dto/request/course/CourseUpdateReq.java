package com.myproject.elearning.dto.request.course;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateReq {
    String description;

    @PositiveOrZero
    int duration;

    @PositiveOrZero
    BigDecimal price;
}
