package com.myproject.elearning.dto.response.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostGetRes {
    Long id;
    String content;
    Long userId;
    String username;
    Boolean likedByCurrentUser;
    Long likesCount;

    public PostGetRes(Long id, String content, Long userId, String username) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.likedByCurrentUser = false;
        this.likesCount = 0L;
    }
}
