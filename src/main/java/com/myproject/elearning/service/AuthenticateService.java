package com.myproject.elearning.service;

import com.myproject.elearning.dto.request.LoginRequest;
import com.myproject.elearning.dto.request.LogoutRequest;
import com.myproject.elearning.dto.response.JwtAuthenticationResponse;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.web.rest.utils.JwtTokenUtil;
import java.time.Instant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService {

    private final JwtDecoder jwtDecoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenValidationService tokenValidationService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticateService(
            JwtDecoder jwtDecoder,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            TokenValidationService tokenValidationService,
            CustomUserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil) {
        this.jwtDecoder = jwtDecoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenValidationService = tokenValidationService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public JwtAuthenticationResponse authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        /* https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-authentication */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenUtil.generateAccessToken(authentication);
        String refreshToken = jwtTokenUtil.generateAndStoreNewRefreshToken(authentication.getName());
        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    public JwtAuthenticationResponse refresh(Jwt jwt) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = jwtTokenUtil.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenUtil.generateAndStoreNewRefreshToken(authentication.getName());
        return new JwtAuthenticationResponse(newAccessToken, newRefreshToken);
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
}
