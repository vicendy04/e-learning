package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.Enrollment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentEditRes {
    Long id;
    Enrollment.EnrollmentStatus status;
    String reasonForDropping;
}
