package com.myproject.elearning.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Object to return as body in JWT Authentication.
 */
@Getter
@AllArgsConstructor
public class TokenPair {
    private String accessToken;
    private String refreshToken;
}
