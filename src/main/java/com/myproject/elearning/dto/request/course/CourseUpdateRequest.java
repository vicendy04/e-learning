package com.myproject.elearning.dto.request.course;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {
    private String description;

    @PositiveOrZero
    private int duration;

    @PositiveOrZero
    private BigDecimal price;

    private String category;
}