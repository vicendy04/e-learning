package com.myproject.elearning.service;

import com.myproject.elearning.domain.RevokedToken;
import com.myproject.elearning.repository.RefreshTokenRepository;
import com.myproject.elearning.repository.RevokedTokenRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TokenService {
    RevokedTokenRepository revokedTokenRepository;
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void revokeToken(String jti, Instant expireTime) {
        revokedTokenRepository.save(RevokedToken.from(jti, expireTime));
    }

    public boolean isTokenRevoked(String jti) {
        return revokedTokenRepository.existsById(jti);
    }

    /**
     * Checks if the refresh token in the Jwt matches the one stored for the user.
     * check if valid for device name and user matching
     *
     * @param jwt the Jwt to validate
     * @return true if the Jwt is valid, false otherwise
     */
    public boolean isRefreshTokenValidForUser(Jwt jwt) {
        String userId = jwt.getSubject();
        String refreshToken = jwt.getTokenValue();
        if (userId == null || refreshToken == null) {
            return false;
        }
        return refreshTokenRepository.existsByTokenAndUserIdAndDeviceName(
                refreshToken, Long.parseLong(userId), "A"); //        hardcode
    }
}
