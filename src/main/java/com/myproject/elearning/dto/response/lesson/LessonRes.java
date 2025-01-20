package com.myproject.elearning.dto.response.lesson;

import com.myproject.elearning.domain.Lesson.LessonType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRes {
    Long id;
    String title;
    String contentUrl;
    Integer orderIndex;
    LessonType contentType;
    Boolean isFreePreview;
    Long chapterId;
}
