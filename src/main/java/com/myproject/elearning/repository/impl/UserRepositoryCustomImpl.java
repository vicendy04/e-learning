package com.myproject.elearning.repository.impl;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.dto.request.user.UserPreferencesData;
import com.myproject.elearning.repository.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private static final int BATCH_SIZE = 10;
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public UserRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public void bulkAddPref(UserPreferencesData userPreferences) {
        String sql = "INSERT IGNORE INTO user_preferences (user_id, topic_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, userPreferences.topicIds(), BATCH_SIZE, (ps, topicId) -> {
            ps.setLong(1, userPreferences.userId());
            ps.setLong(2, topicId);
        });
    }

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
