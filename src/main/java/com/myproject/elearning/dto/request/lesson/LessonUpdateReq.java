package com.myproject.elearning.dto.request.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateReq {
    String title;
    String contentUrl;
    LessonType contentType;
}
