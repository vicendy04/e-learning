package com.myproject.elearning.service;

import static com.myproject.elearning.security.SecurityUtils.AUTHORITIES_KEY;
import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.LoginRequest;
import com.myproject.elearning.dto.request.LogoutRequest;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    private final TokenValidationService tokenValidationService;

    public AuthenticateService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            UserService userService,
            UserRepository userRepository,
            TokenValidationService tokenValidationService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenValidationService = tokenValidationService;
    }

    public Authentication authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant expirationTime = now.plus(this.accessTokenValidityInSeconds, ChronoUnit.SECONDS);

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationTime)
                .subject(authentication.getName())
                .id((UUID.randomUUID().toString()))
                .claim(AUTHORITIES_KEY, authorities)
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
     * Add revoked tokens to blocklist.
     */
    public void revoke(LogoutRequest logoutRequest) {
        String token = logoutRequest.getToken();
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token is null or empty");
        }
        Jwt decode = jwtDecoder.decode(token);
        String jti = decode.getId();
        Instant expireTime = decode.getExpiresAt();
        tokenValidationService.revokeToken(jti, expireTime);
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
