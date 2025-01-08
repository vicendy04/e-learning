package com.myproject.elearning.dto.response.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonGetRes {
    Long id;
    String title;
    String contentUrl;
    Integer orderIndex;
    LessonType contentType;
    Long chapterId;
}
