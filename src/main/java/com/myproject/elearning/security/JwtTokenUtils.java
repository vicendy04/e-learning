package com.myproject.elearning.security;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;

import com.myproject.elearning.service.UserService;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * Utility class for jwt.
 */
@RequiredArgsConstructor
@Component
public final class JwtTokenUtils {

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.token-refresh-threshold}")
    private Long tokenRefreshThresholdInSeconds;

    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenValidityInSeconds))
                .subject(authentication.getName())
                .id((UUID.randomUUID().toString()))
                .claim(CLAIM_KEY_AUTHORITIES, authorities)
                .build();

        return encodeClaims(claims);
    }

    private Instant generateExpirationDate(Instant now, long expiration) {
        return now.plus(expiration, ChronoUnit.SECONDS);
    }

    private String encodeClaims(JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateRefreshToken(String id, Instant now, Instant expirationDate) {
        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationDate)
                .subject(id)
                .id((UUID.randomUUID().toString()))
                .build();

        return encodeClaims(claims);
    }

    public String generateAndStoreNewRefreshToken(String id) {
        Instant now = Instant.now();
        Instant expirationDate = now.plusSeconds(refreshTokenValidityInSeconds);

        String newRefreshToken = generateRefreshToken(id, now, expirationDate);
        userService.overrideRefreshToken(id, newRefreshToken, expirationDate);
        return newRefreshToken;
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
}
