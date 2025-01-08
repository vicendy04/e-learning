package com.myproject.elearning.dto.request.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateReq {
    String title;
    String contentUrl;
    LessonType contentType;

    @NotNull(message = "Phải nhập course id vào")
    Long courseId;
}
