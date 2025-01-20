package com.myproject.elearning.dto.request.chapter;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterCreateReq {
    @NotBlank(message = "Tiêu đề không được để trống")
    String title;

    String description;
}
