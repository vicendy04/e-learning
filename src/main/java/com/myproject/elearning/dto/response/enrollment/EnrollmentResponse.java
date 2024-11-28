package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.Course;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    Long id;
    UserInfo user;
    CourseInfo course;
    Instant enrolledAt;
    String status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserInfo {
        Long id;
        String email;
        String username;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CourseInfo {
        Long id;
        String title;
        String description;
        BigDecimal price;
        Course.CourseCategory category;
    }
}
