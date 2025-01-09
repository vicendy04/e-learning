package com.myproject.elearning.dto.response.chapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterGetRes {
    Long id;
    String title;
    String description;
    Integer orderIndex;
    Long courseId;
}
