package com.myproject.elearning.dto.response.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostListRes {
    Long id;
    String content;
    //    Integer likeCount;
}
