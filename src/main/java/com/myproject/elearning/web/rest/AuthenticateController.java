package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapErrorResponse;
import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.request.LoginRequest;
import com.myproject.elearning.dto.request.LogoutRequest;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.JwtAuthenticationResponse;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.service.AuthenticateService;
import com.myproject.elearning.web.rest.utils.CookieUtils;
import com.myproject.elearning.web.rest.utils.JwtTokenUtil;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticateController {
    private final AuthenticateService authenticateService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticateController(
            AuthenticateService authenticateService,
            JwtTokenUtil jwtTokenUtil,
            CustomUserDetailsService userDetailsService) {
        this.authenticateService = authenticateService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authorize(
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse authenticationResponse = authenticateService.authenticate(loginRequest);
        ApiResponse<JwtAuthenticationResponse> response = wrapSuccessResponse("Success", authenticationResponse);
        ResponseCookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(
                authenticationResponse.getRefreshToken(), refreshTokenValidityInSeconds);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest logoutRequest) {
        authenticateService.revoke(logoutRequest);
        ApiResponse<Void> response = wrapSuccessResponse("Log out successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    /**
     * REST request to check if the current user is authenticated.
     *
     * @param principal the authentication principal.
     * @return the login name if the user is authenticated.
     */
    @GetMapping(value = "/info")
    public ResponseEntity<ApiResponse<String>> isAuthenticated(Principal principal) {
        ApiResponse<String> response;
        if (Objects.isNull(principal)) {
            response = wrapErrorResponse("Not authenticated", "GUEST");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            response = wrapSuccessResponse("Authenticated", principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(
            @CookieValue(name = "refresh_token") String refreshToken) {
        Jwt jwt = jwtTokenUtil.parseAndValidateRefreshToken(refreshToken);
        if (jwtTokenUtil.isRefreshTokenValidForUser(jwt)) {
            JwtAuthenticationResponse authenticationResponse = authenticateService.refresh(jwt);
            ApiResponse<JwtAuthenticationResponse> response = wrapSuccessResponse("Success", authenticationResponse);
            ResponseCookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(
                    authenticationResponse.getRefreshToken(), refreshTokenValidityInSeconds);
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(wrapErrorResponse("Invalid refresh token", null));
        }
    }
}
