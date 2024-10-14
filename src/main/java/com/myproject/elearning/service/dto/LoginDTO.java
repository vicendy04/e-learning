package com.myproject.elearning.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
