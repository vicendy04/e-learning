package com.myproject.elearning.dto.request.lesson;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDelReq {
    @NotNull(message = "Phải nhập course id vào")
    Long courseId;
}
