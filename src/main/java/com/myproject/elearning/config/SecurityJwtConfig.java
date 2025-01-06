package com.myproject.elearning.config;

import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.exception.problemdetails.TokenException;
import com.myproject.elearning.service.TokenService;
import com.myproject.elearning.service.redis.RedisTokenService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;

@RequiredArgsConstructor
@Configuration
public class SecurityJwtConfig {
    @Value(value = "${jwt.base64-secret}")
    private String jwtKey;

    private final TokenService tokenService;
    private final RedisTokenService redisTokenService;

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
                String jti = jwt.getId();
                Instant expiresAt = jwt.getExpiresAt();
                if (jti == null) {
                    throw new JwtException("Missing token identifier (jti)");
                }
                if (redisTokenService.isTokenRevoked(jti)) {
                    throw new JwtException("Token has been revoked");
                } else if (tokenService.isTokenRevoked(jti)) {
                    redisTokenService.revokeToken(jti, expiresAt);
                    throw new JwtException("Token has been revoked");
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
