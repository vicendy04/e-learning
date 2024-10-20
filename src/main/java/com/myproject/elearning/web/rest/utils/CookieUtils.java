package com.myproject.elearning.web.rest.utils;

import org.springframework.http.ResponseCookie;

public final class CookieUtils {

    private CookieUtils() {}

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public static ResponseCookie createRefreshTokenCookie(String refreshToken, long maxAgeInSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeInSeconds)
                .sameSite("Lax")
                .build();
    }
}
