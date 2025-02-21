package com.myproject.elearning.dto.request.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateReq {
    @NotBlank(message = "Tiêu đề không được để trống")
    String title;

    String contentUrl;

    @NotNull(message = "Loại bài học không được để trống")
    LessonType contentType;

    Boolean isFreePreview = false;
}
