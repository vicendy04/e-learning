package com.myproject.elearning.service;

import com.myproject.elearning.domain.RevokedToken;
import com.myproject.elearning.repository.RevokedTokenRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BlackListService {
    RevokedTokenRepository revokedTokenRepository;

    public void revokeToken(String jti, Instant expireTime) {
        revokedTokenRepository.save(RevokedToken.from(jti, expireTime));
    }

    public boolean isTokenRevoked(String jti) {
        return revokedTokenRepository.existsById(jti);
    }
}
