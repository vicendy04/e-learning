package com.myproject.elearning.repository;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.dto.request.user.UserPreferencesData;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<UserAuth> findAuthDTOByEmail(String email);

    void bulkAddPref(UserPreferencesData likes);
}
