package com.myproject.elearning.dto.response.chapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterRes extends RepresentationModel<ChapterRes> {
    Long id;
    String title;
    String description;
    Integer orderIndex;
}
