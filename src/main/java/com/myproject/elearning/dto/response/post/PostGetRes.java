package com.myproject.elearning.dto.response.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostGetRes {
    Long id;
    String content;
    //    Integer likeCount;
    Long userId;
    //    String username;
}
