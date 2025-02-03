package com.myproject.elearning.dto.request.enrollment;

import com.myproject.elearning.domain.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollStatusUpdateReq {
    @NotNull(message = "Trạng thái không được để trống")
    EnrollmentStatus status;

    String reasonForDropping;
}
