package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.Enrollment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentEditRes {
    Long id;
    Enrollment.EnrollmentStatus status;
    String reasonForDropping;
}
