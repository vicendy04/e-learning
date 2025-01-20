package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseGetRes extends RepresentationModel<CourseGetRes> {
    Long id;
    String title;
    String description;
    Integer duration;
    BigDecimal price;
    Course.Level level;
    String thumbnailUrl;

    Integer enrolledCount;
    Integer reviewCount;

    Integer totalLessons; // Todo: tạo course, chapter luôn 1 lần

    Long instructorId;
    String instructorName;
    String imageUrl;

    Long topicId;
    String topicName;
}
