package com.myproject.elearning.dto.response.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    Long likesCount;

    public PostGetRes(Long id, String content, Long userId, String username) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.likesCount = 0L;
    }
}
