package com.myproject.elearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInput {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
