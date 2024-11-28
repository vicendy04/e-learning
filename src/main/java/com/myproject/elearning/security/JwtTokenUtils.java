package com.myproject.elearning.security;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for jwt.
 */
@Component
public final class JwtTokenUtils {
    @Value("${jwt.token-refresh-threshold}")
    private Long tokenRefreshThresholdInSeconds;

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
}
