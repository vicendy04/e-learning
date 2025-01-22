package com.myproject.elearning.rest.auth;

import static com.myproject.elearning.rest.utils.ResponseUtils.errorRes;
import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.dto.request.auth.ChangePasswordReq;
import com.myproject.elearning.dto.request.auth.LoginReq;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.rest.utils.CookieUtils;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.AuthService;
import com.myproject.elearning.service.TokenService;
import com.myproject.elearning.service.UserService;
import com.myproject.elearning.service.redis.AuthRedisService;
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
    JwtTokenUtils jwtTokenUtils;
    JwtDecoder refreshTokenDecoder;
    AuthRedisService authRedisService;

    @PostMapping("/login")
    public ResponseEntity<ApiRes<String>> authorize(@Valid @RequestBody LoginReq loginReq) {
        UserAuth userAuth = authRedisService.getAside(loginReq.getEmail());
        Authentication authentication = authService.authenticate(loginReq, userAuth);
        TokenPair tokenPair = authService.generateTokenPair(authentication);
        ResponseCookie cookie = CookieUtils.addRefreshCookie(tokenPair.getRefreshToken());
        var res = successRes("Success", tokenPair.getAccessToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiRes<Object>> changePassword(@RequestBody @Valid ChangePasswordReq request) {
        authService.changePassword(request);
        var res = successRes("Change password successfully", null);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Object>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenCookie) throws ParseException {
        if (refreshTokenCookie == null) {
            var res = errorRes("Refresh token is missing", null);
            return ResponseEntity.badRequest().body(res);
        }
        Jwt jwt = refreshTokenDecoder.decode(refreshTokenCookie);
        authService.logout(jwt);
        ResponseCookie cookie = CookieUtils.delRefreshCookie();
        var res = successRes("Log out successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @GetMapping(value = "/info")
    public ResponseEntity<ApiRes<String>> isAuthenticated(Principal principal) {
        ApiRes<String> res;
        if (Objects.isNull(principal)) {
            res = errorRes("Not authenticated", "GUEST");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else {
            res = successRes("Authenticated", principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<ApiRes<String>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String cookieValue) throws ParseException {
        Jwt jwt = jwtTokenUtils.extractJwtFromCookie(cookieValue).orElseThrow(AnonymousUserEx::new);
        if (!tokenService.isRefreshTokenValidForUser(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes("Invalid refresh token", null));
        }
        String email = userService.findEmailById(Long.valueOf(jwt.getSubject()));
        UserAuth userAuth = authRedisService.getAside(email);
        Authentication authentication = SecurityUtils.setAuthContext(userAuth);
        TokenPair tokenPair = authService.generateTokenPairForRefresh(jwt, authentication);
        ResponseCookie cookie = CookieUtils.addRefreshCookie(tokenPair.getRefreshToken());
        var res = successRes("Success", tokenPair.getAccessToken());
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }
}
