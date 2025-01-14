package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.errorRes;
import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.projection.UserAuthDTO;
import com.myproject.elearning.dto.request.auth.ChangePasswordReq;
import com.myproject.elearning.dto.request.auth.LoginReq;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.AuthService;
import com.myproject.elearning.service.TokenService;
import com.myproject.elearning.service.UserService;
import com.myproject.elearning.service.redis.RedisAuthService;
import com.myproject.elearning.web.rest.utils.CookieUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    AuthService authService;
    UserService userService;
    TokenService tokenService;
    RedisAuthService redisAuthService;
    JwtDecoder refreshTokenDecoder;
    JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiRes<String>> authorize(@Valid @RequestBody LoginReq loginReq) {
        UserAuthDTO userAuthDTO = redisAuthService.getCachedUser(loginReq.getEmail());
        if (userAuthDTO == null) {
            userAuthDTO = userService.findAuthDTOByEmail(loginReq.getEmail());
            redisAuthService.setCachedUser(loginReq.getEmail(), userAuthDTO);
        }
        Authentication authentication = authService.authenticate(loginReq, userAuthDTO);
        TokenPair authenticationResponse = authService.generateTokenPair(authentication);
        ApiRes<String> response = successRes("Success", authenticationResponse.getAccessToken());
        ResponseCookie refreshTokenCookie = CookieUtils.addRefreshCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiRes<Void>> changePassword(@RequestBody @Valid ChangePasswordReq request) {
        authService.changePassword(request);
        ApiRes<Void> response = successRes("Change password successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenCookie) throws ParseException {
        if (refreshTokenCookie == null) {
            ApiRes<Void> response = errorRes("Refresh token is missing", null);
            return ResponseEntity.badRequest().body(response);
        }
        Jwt jwt = refreshTokenDecoder.decode(refreshTokenCookie);
        authService.logout(jwt);
        ApiRes<Void> response = successRes("Log out successfully", null);
        ResponseCookie clearCookie = CookieUtils.delRefreshCookie();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(response);
    }

    @GetMapping(value = "/info")
    public ResponseEntity<ApiRes<String>> isAuthenticated(Principal principal) {
        ApiRes<String> response;
        if (Objects.isNull(principal)) {
            response = errorRes("Not authenticated", "GUEST");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            response = successRes("Authenticated", principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<ApiRes<String>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String cookieValue) throws ParseException {
        Jwt jwt = jwtTokenUtils.extractJwtFromCookie(cookieValue).orElseThrow(AnonymousUserException::new);
        if (!tokenService.isRefreshTokenValidForUser(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes("Invalid refresh token", null));
        }
        String email = userService.findEmailById(Long.valueOf(jwt.getSubject()));
        UserAuthDTO userAuthDTO = redisAuthService.getCachedUser(email);
        if (userAuthDTO == null) {
            userAuthDTO = userService.findAuthDTOByEmail(email);
            redisAuthService.setCachedUser(email, userAuthDTO);
        }
        Authentication authentication = SecurityUtils.setAuthContext(userAuthDTO);
        TokenPair authenticationResponse = authService.generateTokenPairForRefresh(jwt, authentication);
        ApiRes<String> response = successRes("Success", authenticationResponse.getAccessToken());
        ResponseCookie responseCookie = CookieUtils.addRefreshCookie(authenticationResponse.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(response);
    }
}
