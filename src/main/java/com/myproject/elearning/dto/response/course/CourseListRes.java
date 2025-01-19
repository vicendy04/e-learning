package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseListRes extends RepresentationModel<CourseListRes> {
    Long id;
    String title;
    Integer duration;
    Course.CourseCategory category;
    Course.Level level;

    String thumbnailUrl;
    Integer enrolledCount;
    Integer reviewCount;
    Integer totalLessons; // Todo: tạo course, chapter luôn 1 lần
}
