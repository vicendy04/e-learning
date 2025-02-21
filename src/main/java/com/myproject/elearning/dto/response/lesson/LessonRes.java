package com.myproject.elearning.dto.response.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRes {
    Long id;
    String title;
    Integer orderIndex;
    LessonType contentType;
    Boolean isFreePreview;
    Long chapterId;
}
