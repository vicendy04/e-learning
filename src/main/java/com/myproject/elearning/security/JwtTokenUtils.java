package com.myproject.elearning.security;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * Utility class for jwt.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public final class JwtTokenUtils {
    @Value("${jwt.token-refresh-threshold}")
    Long tokenRefreshThresholdInSeconds;

    JwtDecoder refreshTokenDecoder;

    public JwtTokenUtils(
            @Value("${jwt.token-refresh-threshold}") Long tokenRefreshThresholdInSeconds,
            JwtDecoder refreshTokenDecoder) {
        this.tokenRefreshThresholdInSeconds = tokenRefreshThresholdInSeconds;
        this.refreshTokenDecoder = refreshTokenDecoder;
    }

    /**
     * Checks if the given token is near its refresh threshold.
     */
    public boolean isTokenReadyForRefresh(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Instant createdAt = signedJWT.getJWTClaimsSet().getIssueTime().toInstant();
        Instant now = Instant.now();
        Instant refreshThresholdTime = now.minusSeconds(tokenRefreshThresholdInSeconds);
        return now.isAfter(createdAt) && createdAt.isBefore(refreshThresholdTime);
    }

    public SignedJWT getClaims(String token) throws ParseException {
        return SignedJWT.parse(token);
    }

    public Optional<Jwt> extractJwtFromCookie(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(refreshTokenDecoder.decode(cookieValue));
    }
}
