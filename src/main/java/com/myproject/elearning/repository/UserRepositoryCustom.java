package com.myproject.elearning.repository;

import com.myproject.elearning.dto.projection.UserAuth;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserAuth> findAuthDTOByEmail(String email);
}
