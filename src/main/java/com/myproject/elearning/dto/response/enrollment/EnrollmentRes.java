package com.myproject.elearning.dto.response.enrollment;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRes {
    Long id;
    Instant enrolledAt;
    String status;
}
