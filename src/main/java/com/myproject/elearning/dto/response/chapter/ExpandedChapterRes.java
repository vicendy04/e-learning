package com.myproject.elearning.dto.response.chapter;

import com.myproject.elearning.domain.Lesson;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpandedChapterRes {
    Long id;
    String title;
    String description;
    Integer orderIndex;
    List<Item> items;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Item {
        Long id;
        String title;
        String contentUrl;
        Integer orderIndex;
        Lesson.LessonType contentType;
        Boolean isFreePreview = false;
    }
}
