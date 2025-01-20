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
public class ReviewUserRes {
    Long id;
    Integer rating;
    String comment;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long courseId;
    String courseTitle;

    public ReviewUserRes(Long id, Integer rating, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, Long courseId, String courseTitle) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }
}
