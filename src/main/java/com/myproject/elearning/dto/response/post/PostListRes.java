package com.myproject.elearning.dto.response.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostListRes {
    Long id;
    String content;
    //    Integer likeCount;
}
