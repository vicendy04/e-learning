package com.myproject.elearning.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequest {
    @NotBlank
    private String title;

    private String description;

    @PositiveOrZero
    private int duration;

    @PositiveOrZero
    private BigDecimal price;

    private String category;
}
