package com.myproject.elearning.dto.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Object to return as body in JWT Authentication.
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenPair {
    String accessToken;
    String refreshToken;
}
