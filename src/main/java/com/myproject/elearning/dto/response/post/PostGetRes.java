package com.myproject.elearning.dto.response.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostGetRes {
    Long id;
    String content;
    Long userId;
    String username;
    Boolean likedByCurrentUser;

    public PostGetRes(Long id, String content, Long userId, String username) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.likedByCurrentUser = false;
    }
}
