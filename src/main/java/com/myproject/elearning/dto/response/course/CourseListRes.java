package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseListRes {
    Long id;
    String title;
    int duration;
    Course.CourseCategory category;
    Course.Level level;
}
