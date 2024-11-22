package com.myproject.elearning.dto.request.course;

import java.math.BigDecimal;

import com.myproject.elearning.domain.Course;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSearchDTO {
    private String title;
    private String category;
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
