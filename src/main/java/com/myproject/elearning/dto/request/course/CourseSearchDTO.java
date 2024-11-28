package com.myproject.elearning.dto.request.course;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseSearchDTO {
    String title;
    String category;
    String keyword;
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
