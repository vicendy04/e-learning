package com.myproject.elearning.dto.response.review;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
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

    public ReviewUserRes(
            Long id,
            Integer rating,
            String comment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long courseId,
            String courseTitle) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }
}
