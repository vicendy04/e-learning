package com.myproject.elearning.dto.request.course;

import com.myproject.elearning.domain.Course;
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
    Course.CourseCategory category;
    String keyword;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    Course.Level level;
    String language;
    Boolean isFree;
    String instructorName;
    Float minRating;
    Integer maxDuration;
    String sortBy;
    String sortDirection;
}
