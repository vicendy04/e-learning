package com.myproject.elearning.dto;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseData {
    Long id;
    String title;
    String description;
    Integer duration;
    BigDecimal price;
    Course.Level level;
    String thumbnailUrl;

    Integer enrolledCount;
    Integer reviewCount;

    Long instructorId;
    String firstName;
    String lastName;
    String imageUrl;

    Long topicId;
    String topicName;

    public CourseData(
            Long id,
            String title,
            String description,
            Integer duration,
            BigDecimal price,
            Course.Level level,
            String thumbnailUrl,
            Integer enrolledCount,
            Integer reviewCount,
            Long instructorId,
            String firstName,
            String lastName,
            String imageUrl,
            Long topicId,
            String topicName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.price = price;
        this.level = level;
        this.thumbnailUrl = thumbnailUrl;
        this.enrolledCount = enrolledCount;
        this.reviewCount = reviewCount;
        this.instructorId = instructorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
        this.topicId = topicId;
        this.topicName = topicName;
    }
}
