package com.myproject.elearning.dto.response.course;

import java.math.BigDecimal;

import com.myproject.elearning.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Course.CourseCategory category;
}
