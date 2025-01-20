package com.myproject.elearning.dto.request.chapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterUpdateReq {
    String title;
    String description;
}
