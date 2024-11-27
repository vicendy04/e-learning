package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapErrorResponse;
import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.request.auth.LoginRequest;
import com.myproject.elearning.service.AuthenticateService;
import com.myproject.elearning.web.rest.utils.CookieUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticateController {
    private final AuthenticateService authenticateService;
    private final JwtDecoder refreshTokenDecoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authorize(@Valid @RequestBody LoginRequest loginRequest) {
        TokenPair authenticationResponse = authenticateService.authenticate(loginRequest);
        ApiResponse<String> response = wrapSuccessResponse("Success", authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie =
                CookieUtils.createRefreshTokenCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenCookie) throws ParseException {
        if (refreshTokenCookie == null) {
            ApiResponse<Void> response = wrapErrorResponse("Refresh token is missing", null);
            return ResponseEntity.badRequest().body(response);
        }
        authenticateService.revoke(refreshTokenCookie);
        ApiResponse<Void> response = wrapSuccessResponse("Log out successfully", null);
        ResponseCookie clearCookie = CookieUtils.deleteRefreshTokenCookie();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(response);
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
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenCookie) throws ParseException {
        if (refreshTokenCookie == null) {
            ApiResponse<String> response = wrapErrorResponse("Refresh token is missing", null);
            return ResponseEntity.badRequest().body(response);
        }
        Jwt jwt = refreshTokenDecoder.decode(refreshTokenCookie);
        if (!authenticateService.isRefreshTokenValidForUser(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(wrapErrorResponse("Invalid refresh token", null));
        }
        TokenPair authenticationResponse = authenticateService.refresh(jwt);
        ApiResponse<String> response = wrapSuccessResponse("Success", authenticationResponse.getAccessToken());
        ResponseCookie responseCookie = CookieUtils.createRefreshTokenCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(response);
    }
}
