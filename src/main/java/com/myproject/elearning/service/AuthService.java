package com.myproject.elearning.service;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;

import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.request.auth.LoginRequest;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.cache.RedisTokenBlacklistService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {
    @NonFinal
    @Value("${jwt.access-token-expiration}")
    long accessTokenValidityInSeconds;

    @NonFinal
    @Value("${jwt.refresh-token-expiration}")
    long refreshTokenValidityInSeconds;

    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenBlacklistService tokenBlacklistService;
    RedisTokenBlacklistService redisTokenBlacklistService;
    CustomUserDetailsService userDetailsService;
    JwtTokenUtils jwtTokenUtils;
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    JwtEncoder jwtEncoder;
    UserService userService;

    public TokenPair authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        /* https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-authentication */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public TokenPair refresh(Jwt jwt) throws ParseException {
        // double - check user is existing!
        String email = userRepository
                .findEmailByUserId(Long.valueOf(jwt.getSubject()))
                .orElseThrow(() -> new InvalidIdException("Email not found!"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = generateAccessToken(authentication);
        String newRefreshToken;
        //      * Checks if the given token is near its refresh threshold.
        if (jwtTokenUtils.isTokenReadyForRefresh(jwt.getTokenValue())) {
            this.revoke(jwt.getTokenValue());
            newRefreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        } else {
            newRefreshToken = jwt.getTokenValue(); // old token
        }
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    public void revoke(String token) throws ParseException {
        SignedJWT signedJWT = jwtTokenUtils.getClaims(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Instant expireTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        tokenBlacklistService.revokeToken(jti, expireTime); // db save
        redisTokenBlacklistService.revokeToken(jti, expireTime); // redis set
    }

    /**
     * Checks if the refresh token in the Jwt matches the one stored for the user.
     * check if valid for device name and user matching
     *
     * @param jwt the Jwt to validate
     * @return true if the Jwt is valid, false otherwise
     */
    public boolean isRefreshTokenValidForUser(Jwt jwt) {
        if (jwt == null) {
            return false;
        }
        String userId = jwt.getSubject();
        String refreshToken = jwt.getTokenValue();
        if (userId == null || refreshToken == null) {
            return false;
        }
        return refreshTokenRepository.existsByTokenAndUserIdAndDeviceName(
                refreshToken, Long.parseLong(userId), "A"); //        hardcode
    }

    private String generateAccessToken(Authentication authentication) {
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

    private String encodeClaims(JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private String generateRefreshToken(String id, Instant now, Instant expirationDate) {
        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(expirationDate)
                .subject(id)
                .id((UUID.randomUUID().toString()))
                .build();

        return encodeClaims(claims);
    }

    private String generateAndStoreNewRefreshToken(String id) {
        Instant now = Instant.now();
        Instant expirationDate = now.plusSeconds(refreshTokenValidityInSeconds);

        String newRefreshToken = generateRefreshToken(id, now, expirationDate);
        userService.overrideRefreshToken(id, newRefreshToken, expirationDate);
        return newRefreshToken;
    }
}
