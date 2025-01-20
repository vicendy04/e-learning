package com.myproject.elearning.dto.request.enrollment;

import com.myproject.elearning.domain.Enrollment;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollStatusUpdateReq {
    @NotNull(message = "Trạng thái không được để trống")
    Enrollment.EnrollmentStatus status;

    String reasonForDropping;
}
