package com.myproject.elearning.repository;

import com.myproject.elearning.domain.RefreshToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    //    @Query(value = "select count(rt.id) > 0 from refresh_tokens rt " +
    //            "join users u on u.id = rt.user_id " +
    //            "where rt.token = ?1 " +
    //            "and u.email = ?2 " +
    //            "and rt.device_name = ?3", nativeQuery = true)
    //    boolean existsByTokenAndUserEmailAndDeviceName(String token,
    //                                                   String email,
    //                                                   String deviceName);
    boolean existsByTokenAndUserEmailAndDeviceName(String token, String email, String deviceName);

    List<RefreshToken> findAllByUserId(Long userId);
}
