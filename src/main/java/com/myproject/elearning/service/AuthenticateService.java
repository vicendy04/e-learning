package com.myproject.elearning.service;

import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.request.auth.LoginRequest;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.security.JwtTokenUtils;
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
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

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
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = jwtTokenUtils.generateAccessToken(authentication);
        String newRefreshToken;
        if (jwtTokenUtils.isTokenReadyForRefresh(jwt.getTokenValue())) {
            revoke(jwt.getTokenValue());
            newRefreshToken = jwtTokenUtils.generateAndStoreNewRefreshToken(authentication.getName());
        } else {
            newRefreshToken = jwt.getTokenValue(); // old token
        }
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    /**
     * Add revoked tokens to blocklist.
     */
    @Transactional
    public void revoke(String token) throws ParseException {
        SignedJWT signedJWT = jwtTokenUtils.getClaims(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Instant expireTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        tokenBlacklistService.revokeToken(jti, expireTime);
    }
}
