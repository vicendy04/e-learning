package com.myproject.elearning.dto.response.course;

import com.myproject.elearning.domain.Course;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseListResponse {
    Long id;
    String title;
    int duration;
    Course.CourseCategory category;
}
