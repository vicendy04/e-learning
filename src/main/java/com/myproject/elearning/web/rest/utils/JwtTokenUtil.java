package com.myproject.elearning.web.rest.utils;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;
import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.UserService;
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
public class JwtTokenUtil {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    private final UserService userService;
    private final UserRepository userRepository;

    public JwtTokenUtil(
            JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
        this.userRepository = userRepository;
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

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(generateExpirationDate(now, refreshTokenValidityInSeconds))
                .subject(email)
                .build();

        return encodeClaims(claims);
    }

    public String generateAndStoreNewRefreshToken(String email) {
        String newRefreshToken = generateRefreshToken(email);
        userService.updateUserWithRefreshToken(email, newRefreshToken);
        return newRefreshToken;
    }
    /**
     * Verifies the given refresh token and return Jwt.
     *
     * @param refreshToken the refresh token to verify
     * @return an Optional containing the decoded Jwt if valid, or empty if invalid
     */
    public Jwt parseAndValidateRefreshToken(String refreshToken) throws JwtException {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtException("Refresh token is null or empty");
        }
        return jwtDecoder.decode(refreshToken);
    }

    /**
     * Checks if the refresh token in the Jwt matches the one stored for the user.
     *
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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidIdException("Email not found!"));
        return refreshToken.equals(user.getRefreshTokenValue());
    }
}
