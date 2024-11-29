package com.myproject.elearning.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterReq {
    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(max = 50)
    String username;

    @NotBlank
    @Size(min = 4)
    String password;
}
