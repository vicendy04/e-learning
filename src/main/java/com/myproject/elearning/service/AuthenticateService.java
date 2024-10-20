package com.myproject.elearning.service;

import static com.myproject.elearning.security.SecurityUtils.AUTHORITIES_KEY;
import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.dto.request.LoginRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthenticateService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            UserService userService,
            UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public Authentication authenticate(LoginRequest loginRequest) {
        // Load username/password to Security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        // This requires writing a loadUserByUsername method
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Load authentication information into SecurityContext (if authentication is successful)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public String generateAccessToken(String email) {
        //        String authorities = authentication.getAuthorities().stream()
        //                .map(GrantedAuthority::getAuthority)
        //                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant expirationTime = now.plus(this.accessTokenValidityInSeconds, ChronoUnit.SECONDS);

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(email)
                .claim(AUTHORITIES_KEY, "ROLE_USER")
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(this.refreshTokenValidityInSeconds, ChronoUnit.SECONDS);

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(email)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
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
        User user = userRepository.findByEmail(email).orElseThrow();
        return refreshToken.equals(user.getRefreshTokenValue());
    }
}
