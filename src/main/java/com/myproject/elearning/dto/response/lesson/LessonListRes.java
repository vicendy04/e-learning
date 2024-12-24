package com.myproject.elearning.dto.response.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonListRes {
    Long id;
    String title;
    String contentUrl;
    Integer orderIndex;
    LessonType contentType;
}
