package com.myproject.elearning.dto.response;

import lombok.*;

/**
 * Object to return as body in JWT Authentication.
 */
@Getter
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
}
