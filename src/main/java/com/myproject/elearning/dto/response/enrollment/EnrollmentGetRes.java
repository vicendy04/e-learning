package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentGetRes {
    Long id;
    Instant enrolledAt;
    Enrollment.EnrollmentStatus status;
    CourseInfo course;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CourseInfo {
        Long id;
        String title;
        String description;
        BigDecimal price;
        Course.Level level;
        String thumbnailUrl;
    }
}
