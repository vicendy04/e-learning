package com.myproject.elearning.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordReq {
    @Size(min = 6, message = "INVALID_PASSWORD")
    String currentPassword;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String newPassword;

    @NotBlank(message = "Confirm password is required")
    String confirmPassword;
}
