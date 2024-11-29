package com.myproject.elearning.web.rest.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Util class for making cookies.
 */
@Component
public final class CookieUtils {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    private static long staticRefreshTokenValidityInSeconds;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private CookieUtils() {}

    @PostConstruct
    public void init() {
        staticRefreshTokenValidityInSeconds = this.refreshTokenValidityInSeconds;
    }

    public static ResponseCookie addRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(staticRefreshTokenValidityInSeconds)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie delRefreshCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
