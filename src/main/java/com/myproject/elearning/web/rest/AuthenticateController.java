package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapErrorResponse;
import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.request.LoginRequest;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.JwtAuthenticationResponse;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.service.AuthenticateService;
import com.myproject.elearning.web.rest.utils.CookieUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthenticateController {
    private final AuthenticateService authenticateService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInSeconds;

    private final CustomUserDetailsService userDetailsService;

    public AuthenticateController(
            AuthenticateService authenticateService, CustomUserDetailsService userDetailsService) {
        this.authenticateService = authenticateService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authorize(
            @Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticateService.authenticate(loginRequest);

        String accessToken = authenticateService.generateAccessToken(authentication);
        String refreshToken = authenticateService.generateAndStoreNewRefreshToken(authentication.getName());

        JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse(accessToken, refreshToken);
        ApiResponse<JwtAuthenticationResponse> response = wrapSuccessResponse("Success", authenticationResponse);
        ResponseCookie refreshTokenCookie =
                CookieUtils.createRefreshTokenCookie(refreshToken, refreshTokenValidityInSeconds);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    /**
     * REST request to check if the current user is authenticated.
     *
     * @param principal the authentication principal.
     * @return the login name if the user is authenticated.
     */
    @GetMapping(value = "/authenticate")
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
        Jwt jwt = authenticateService.parseAndValidateRefreshToken(refreshToken);
        if (authenticateService.isRefreshTokenValidForUser(jwt)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // credentials
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String newAccessToken = authenticateService.generateAccessToken(authenticationToken);
            String newRefreshToken = authenticateService.generateAndStoreNewRefreshToken(jwt.getSubject());

            JwtAuthenticationResponse authenticationResponse =
                    new JwtAuthenticationResponse(newAccessToken, newRefreshToken);
            ApiResponse<JwtAuthenticationResponse> response = wrapSuccessResponse("Success", authenticationResponse);
            ResponseCookie refreshTokenCookie =
                    CookieUtils.createRefreshTokenCookie(newRefreshToken, refreshTokenValidityInSeconds);
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(wrapErrorResponse("Invalid refresh token", null));
        }
    }
}
