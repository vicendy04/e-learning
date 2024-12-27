package com.myproject.elearning.service;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.TokenPair;
import com.myproject.elearning.dto.projection.UserAuthDTO;
import com.myproject.elearning.dto.request.auth.ChangePasswordReq;
import com.myproject.elearning.dto.request.auth.LoginReq;
import com.myproject.elearning.exception.constants.ErrorMessageConstants;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidCredentialsException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.security.CustomUserDetailsService;
import com.myproject.elearning.security.JwtTokenUtils;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.RedisAuthService;
import com.myproject.elearning.service.redis.RedisBlackListService;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    AuthenticationManagerBuilder authenticationManagerBuilder;
    BlackListService blackListService;
    RedisBlackListService redisBlackListService;
    CustomUserDetailsService userDetailsService;
    JwtTokenUtils jwtTokenUtils;
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    JwtEncoder jwtEncoder;
    UserService userService;
    PasswordEncoder passwordEncoder;
    RedisAuthService redisAuthService;

    public TokenPair authenticate(LoginReq loginReq) {
        // Get user from cache or database
        Object cachedUser = redisAuthService.getCachedUser(loginReq.getUsernameOrEmail());
        UserAuthDTO userAuthDTO;

        if (cachedUser instanceof UserAuthDTO cached) {
            userAuthDTO = cached;
        } else {
            userAuthDTO = userRepository
                    .findAuthDTOByEmail(loginReq.getUsernameOrEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
            redisAuthService.setCachedUser(loginReq.getUsernameOrEmail(), userAuthDTO);
        }

        // Verify password
        if (!passwordEncoder.matches(loginReq.getPassword(), userAuthDTO.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        List<GrantedAuthority> authorities = userAuthDTO.getRoleNames().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create authentication token
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userAuthDTO.getId().toString(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void changePassword(ChangePasswordReq request) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        User user = userRepository.findById(curUserId).orElseThrow(() -> new InvalidIdException(curUserId));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new InvalidIdException(ErrorMessageConstants.CURRENT_PASSWORD_INVALID);
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidIdException(ErrorMessageConstants.PASSWORD_EXISTED);
        }
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword()))
            throw new InvalidIdException(ErrorMessageConstants.CONFIRM_PASSWORD_INVALID);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public TokenPair refresh(Jwt jwt) throws ParseException {
        // double - check user is existing!
        String email = userRepository
                .findEmailByUserId(Long.valueOf(jwt.getSubject()))
                .orElseThrow(() -> new InvalidIdException("Email not found!"));

        Object cachedUser = redisAuthService.getCachedUser(email);
        UserAuthDTO userAuthDTO;
        if (cachedUser instanceof UserAuthDTO cached) {
            userAuthDTO = cached;
        } else {
            userAuthDTO = userRepository
                    .findAuthDTOByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
            redisAuthService.setCachedUser(email, userAuthDTO);
        }
        List<GrantedAuthority> authorities = userAuthDTO.getRoleNames().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userAuthDTO.getId().toString(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = generateAccessToken(authentication);
        String newRefreshToken;
        // Checks if the given token is near its refresh threshold.
        if (jwtTokenUtils.isTokenReadyForRefresh(jwt.getTokenValue())) {
            revoke(jwt.getTokenValue());
            newRefreshToken = generateAndStoreNewRefreshToken(authentication.getName());
        } else {
            newRefreshToken = jwt.getTokenValue(); // Keep existing refresh token
        }
        return new TokenPair(newAccessToken, newRefreshToken);
    }

    public void logout(Jwt jwt) throws ParseException {
        revoke(jwt.getTokenValue());
    }

    public void revoke(String token) throws ParseException {
        SignedJWT signedJWT = jwtTokenUtils.getClaims(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Instant expireTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        blackListService.revokeToken(jti, expireTime);
        redisBlackListService.revokeToken(jti, expireTime);
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
