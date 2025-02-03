package com.myproject.elearning.dto.response.enrollment;

import com.myproject.elearning.domain.enums.EnrollmentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentEditRes {
    Long id;
    EnrollmentStatus status;
    String reasonForDropping;
}
