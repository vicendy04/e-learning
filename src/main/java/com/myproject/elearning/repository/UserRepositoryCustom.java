package com.myproject.elearning.repository;

import com.myproject.elearning.dto.projection.UserAuthDTO;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserAuthDTO> findAuthDTOByEmail(String email);
}
