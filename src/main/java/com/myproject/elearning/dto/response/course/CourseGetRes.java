package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseGetRes {
    Long id;
    String title;
    String description;
    Integer duration;
    BigDecimal price;
    Course.CourseCategory category;
    Course.Level level;
    String thumbnailUrl;

    Integer enrolledCount;
    Integer reviewCount;

    Integer totalLessons; // Todo: tạo course, chapter luôn 1 lần

    Long instructorId;
    String instructorName;
    String imageUrl;
}
