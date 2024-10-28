package com.myproject.elearning.config;

import static com.myproject.elearning.security.SecurityUtils.JWT_ALGORITHM;

import com.myproject.elearning.service.TokenValidationService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class SecurityJwtConfiguration {
    @Value(value = "${jwt.base64-secret}")
    private String jwtKey;

    private final TokenValidationService tokenValidationService;

    public SecurityJwtConfiguration(TokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @SuppressWarnings({"Convert2Lambda"})
    @Bean
    public JwtDecoder accessTokenDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                Jwt jwt = jwtDecoder.decode(token);
                tokenValidationService.checkTokenRevocationStatus(jwt);
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

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}
