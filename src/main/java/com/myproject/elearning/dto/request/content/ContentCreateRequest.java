package com.myproject.elearning.dto.request.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentCreateRequest {
    @NotBlank
    String title;

    @PositiveOrZero
    Integer orderIndex;

    @Pattern(
            regexp = "^(ASSIGNMENT|CODE_EXERCISE|DOCUMENT|PRESENTATION|QUIZ|VIDEO)$",
            message = "Invalid content type.")
    String contentType;

    @Pattern(regexp = "^(PUBLISHED|DRAFT)$", message = "Invalid content status.")
    String status;

    //    @NotNull
    //   Long courseId;
}
