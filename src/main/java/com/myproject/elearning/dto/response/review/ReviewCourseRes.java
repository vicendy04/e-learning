package com.myproject.elearning.dto.response.review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCourseRes {
    Long id;
    Integer rating;
    String comment;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long userId;
    String username;
    String imageUrl;

    public ReviewCourseRes(Long id, Integer rating, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, Long userId, String username, String imageUrl) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.username = username;
        this.imageUrl = imageUrl;
    }
}
