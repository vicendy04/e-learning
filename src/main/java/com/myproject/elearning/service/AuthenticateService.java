package com.myproject.elearning.service;

import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.request.auth.LoginRequest;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.service.cache.RedisTokenBlacklistService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTokenBlacklistService redisTokenBlacklistService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenPair authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        /* https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-authentication */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenUtils.generateAccessToken(authentication);
        String refreshToken = jwtTokenUtils.generateAndStoreNewRefreshToken(authentication.getName());
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
        String newAccessToken = jwtTokenUtils.generateAccessToken(authentication);
        String newRefreshToken;
        //      * Checks if the given token is near its refresh threshold.
        if (jwtTokenUtils.isTokenReadyForRefresh(jwt.getTokenValue())) {
            this.revoke(jwt.getTokenValue());
            newRefreshToken = jwtTokenUtils.generateAndStoreNewRefreshToken(authentication.getName());
        } else {
            newRefreshToken = jwt.getTokenValue(); // old token
        }
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Transactional
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
}
