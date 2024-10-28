package com.myproject.elearning.service;

import com.myproject.elearning.domain.RevokedToken;
import com.myproject.elearning.repository.RevokedTokenRepository;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class TokenValidationService {
    private final RevokedTokenRepository revokedTokenRepository;

    public TokenValidationService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public boolean isTokenRevoked(String jti) {
        return revokedTokenRepository.findById(jti).isPresent();
    }

    /**
     * {@link BadJwtException} instead of {@link JwtException} which will be handled by the entry point.
     */
    public void checkTokenRevocationStatus(Jwt jwt) {
        String jti = jwt.getId();
        if (jti == null) {
            throw new BadJwtException("Missing jti!");
        }
        if (isTokenRevoked(jti)) {
            throw new BadJwtException("Token has been revoked");
        }
    }

    public void revokeToken(String jti, Instant expireTime) {
        revokedTokenRepository.save(new RevokedToken(jti, expireTime));
    }
}
