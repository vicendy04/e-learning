package com.myproject.elearning.service;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;

import com.myproject.elearning.constant.ErrorMessageConstants;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.dto.request.auth.ChangePasswordReq;
import com.myproject.elearning.dto.request.auth.LoginReq;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.exception.problemdetails.InvalidCredentialsEx;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.RedisTokenService;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    TokenService tokenService;
    RedisTokenService redisTokenService;
    JwtTokenUtils jwtTokenUtils;
    UserRepository userRepository;
    JwtEncoder jwtEncoder;
    UserService userService;
    PasswordEncoder passwordEncoder;

    @Transactional
    public TokenPair generateTokenPair(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        User user = userRepository.findById(curUserId).orElseThrow(() -> new InvalidIdEx(curUserId));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new InvalidIdEx(ErrorMessageConstants.CURRENT_PASSWORD_INVALID);
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidIdEx(ErrorMessageConstants.PASSWORD_EXISTED);
        }
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword()))
            throw new InvalidIdEx(ErrorMessageConstants.CONFIRM_PASSWORD_INVALID);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public TokenPair generateTokenPairForRefresh(Jwt jwt, Authentication authentication) throws ParseException {
        String newAccessToken = generateAccessToken(authentication);
        String newRefreshToken;
        String tokenValue = jwt.getTokenValue();
        // Checks if the given token is near its refresh threshold.
        if (jwtTokenUtils.isTokenReadyForRefresh(tokenValue)) {
            revoke(tokenValue);
            newRefreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        } else {
            newRefreshToken = tokenValue; // Keep existing refresh token
        }
        return TokenPair.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(Jwt jwt) throws ParseException {
        revoke(jwt.getTokenValue());
    }

    @Transactional
    public void revoke(String token) throws ParseException {
        SignedJWT signedJWT = jwtTokenUtils.getClaims(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Instant expireTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        tokenService.revokeToken(jti, expireTime);
        redisTokenService.revokeToken(jti, expireTime);
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

    public Authentication authenticate(LoginReq loginReq, UserAuth authDTO) {
        // Verify password
        if (!passwordEncoder.matches(loginReq.getPassword(), authDTO.getPassword())) {
            throw new InvalidCredentialsEx("Invalid username or password");
        }
        return SecurityUtils.setAuthContext(authDTO);
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
