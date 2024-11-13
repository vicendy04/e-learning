package com.myproject.elearning.dto.request.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentCreateRequest {
    @NotBlank
    private String title;

    @PositiveOrZero
    private Integer orderIndex;

    @Pattern(regexp = "^(ASSIGNMENT|CODE_EXERCISE|DOCUMENT|PRESENTATION|QUIZ|VIDEO)$", message = "Invalid content type.")
    private String contentType;
    @Pattern(regexp = "^(PUBLISHED|DRAFT)$", message = "Invalid content status.")
    private String status;

//    @NotNull
//    private Long courseId;
}
