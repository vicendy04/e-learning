package com.myproject.elearning.repository;

import com.myproject.elearning.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByTokenAndUserIdAndDeviceName(String token, Long userId, String deviceName);

    Optional<RefreshToken> findByUserIdAndDeviceName(Long userId, String deviceName);
}
