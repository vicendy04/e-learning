package com.myproject.elearning.repository.impl;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<UserAuth> findAuthDTOByEmail(String email) {
        // left join
        Query query = entityManager.createNativeQuery(
                """
				SELECT u.id as id,
					u.email as email,
					u.password as password,
					GROUP_CONCAT(r.name) as roles
				FROM users u
				LEFT JOIN users_roles ur ON u.id = ur.user_id
				LEFT JOIN roles r ON r.id = ur.role_id
				WHERE u.email = ?
				GROUP BY u.id, u.email, u.password
				""",
                UserAuth.class);

        query.setParameter(1, email);
        try {
            UserAuth result = (UserAuth) query.getSingleResult();
            return Optional.ofNullable(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
