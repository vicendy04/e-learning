package com.myproject.elearning.dto.request.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreateReq {
    @NotBlank(message = "Tiêu đề không được để trống")
    String title;

    String contentUrl;

    Integer orderIndex;

    @NotNull(message = "Loại bài học không được để trống")
    LessonType contentType;

    @NotNull(message = "Nhập id course vào")
    Long courseId;
}
