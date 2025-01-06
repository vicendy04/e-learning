package com.myproject.elearning.dto.request.lesson;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDelReq {
    @NotNull(message = "Phải nhập course id vào")
    Long courseId;
}
