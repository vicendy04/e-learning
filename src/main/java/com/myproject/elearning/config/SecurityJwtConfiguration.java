package com.myproject.elearning.config;

import com.myproject.elearning.exception.problemdetails.TokenException;
import com.myproject.elearning.service.TokenBlacklistService;
import com.myproject.elearning.service.cache.RedisTokenBlacklistService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

@RequiredArgsConstructor
@Configuration
public class SecurityJwtConfiguration {
    @Value(value = "${jwt.base64-secret}")
    private String jwtKey;

    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTokenBlacklistService redisTokenBlacklistService;

    @Bean
    public DefaultBearerTokenResolver defaultBearerTokenResolver() {
        return new DefaultBearerTokenResolver();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @SuppressWarnings({"Convert2Lambda"})
    @Bean
    public JwtDecoder refreshTokenDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws TokenException {
                Jwt jwt = jwtDecoder.decode(token);
                try {
                    String jti = jwt.getId();
                    Instant expiresAt = jwt.getExpiresAt();
                    if (jti == null) {
                        throw new JwtException("Missing token identifier (jti)");
                    }
                    if (redisTokenBlacklistService.isTokenRevoked(jti)) {
                        throw new JwtException("Token has been revoked");
                    } else if (tokenBlacklistService.isTokenRevoked(jti)) {
                        redisTokenBlacklistService.revokeToken(jti, expiresAt);
                        throw new JwtException("Token has been revoked");
                    }
                } catch (JwtException e) {
                    throw new TokenException("Error checking token revocation status", e);
                }
                return jwt;
            }
        };
    }

    /**
     * Decodes the JWT and verifies its signature, returning a Jwt object containing the token's information.
     *
     * @return {@link NimbusJwtDecoder} default NimbusJwtDecoder
     */
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
    }

    @Bean
    public BearerTokenAccessDeniedHandler bearerTokenAccessDeniedHandler() {
        return new BearerTokenAccessDeniedHandler();
    }

    @Bean
    public BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint() {
        return new BearerTokenAuthenticationEntryPoint();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}
