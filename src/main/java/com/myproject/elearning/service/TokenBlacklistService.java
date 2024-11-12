package com.myproject.elearning.service;

import com.myproject.elearning.domain.RevokedToken;
import com.myproject.elearning.exception.problemdetails.TokenException;
import com.myproject.elearning.repository.RevokedTokenRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RevokedTokenRepository revokedTokenRepository;

    @Transactional
    public void revokeToken(String jti, Instant expireTime) {
        revokedTokenRepository.save(RevokedToken.from(jti, expireTime));
    }

    public boolean isTokenRevoked(String jti) {
        return revokedTokenRepository.existsById(jti);
    }

    public void checkTokenRevocationStatus(Jwt jwt) throws TokenException {
        try {
            String jti = jwt.getId();
            if (jti == null) {
                throw new JwtException("Missing token identifier (jti)");
            }
            if (isTokenRevoked(jti)) {
                throw new JwtException("Token has been revoked");
            }
        } catch (JwtException e) {
            throw new TokenException("Error checking token revocation status", e);
        }
    }
}
