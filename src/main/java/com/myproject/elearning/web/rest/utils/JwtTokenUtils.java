package com.myproject.elearning.web.rest.utils;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;
import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.service.UserService;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    @Value("${jwt.token-refresh-threshold}")
    private Long tokenRefreshThreshold;

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenUtils(
            JwtEncoder jwtEncoder, UserService userService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(generateExpirationDate(now, accessTokenValidityInSeconds))
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
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateRefreshToken(String email, Instant now, Instant expirationDate) {
        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationDate)
                .subject(email)
                .id((UUID.randomUUID().toString()))
                .build();

        return encodeClaims(claims);
    }

    public String generateAndStoreNewRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expirationDate = generateExpirationDate(now, refreshTokenValidityInSeconds);

        String newRefreshToken = generateRefreshToken(email, now, expirationDate);
        userService.updateUserWithRefreshToken(email, newRefreshToken, expirationDate);
        return newRefreshToken;
    }

    /**
     * Checks if the given token is near its refresh threshold.
     */
    public boolean isTokenReadyForRefresh(String token) throws ParseException {
        SignedJWT signedJWT = getClaims(token);
        Instant createAt = signedJWT.getJWTClaimsSet().getIssueTime().toInstant();
        Instant now = Instant.now();
        Instant refreshThresholdTime = Instant.now().minusSeconds(tokenRefreshThreshold);
        return now.isAfter(createAt) && createAt.isBefore(refreshThresholdTime);
    }

    public SignedJWT getClaims(String token) throws ParseException {
        return SignedJWT.parse(token);
    }

    /**
     * Checks if the refresh token in the Jwt matches the one stored for the user.
     * check if valid for device name and user matching
     * @param jwt the Jwt to validate
     * @return true if the Jwt is valid, false otherwise
     */
    public boolean isRefreshTokenValidForUser(Jwt jwt) {
        if (jwt == null) {
            return false;
        }
        String email = jwt.getSubject();
        String refreshToken = jwt.getTokenValue();

        if (email == null || refreshToken == null) {
            return false;
        }

        return refreshTokenRepository.existsByTokenAndUserEmailAndDeviceName(
                refreshToken, email, "A"); //        hardcode
    }
}
