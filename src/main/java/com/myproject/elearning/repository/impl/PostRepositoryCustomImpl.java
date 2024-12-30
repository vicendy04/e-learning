package com.myproject.elearning.repository.impl;

import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.repository.PostRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PostGetRes> findPostGetResById(Long id) {
        Query query = entityManager.createNativeQuery(
                """
				SELECT
					p.id as id,
					p.content as content,
					u.id as userId,
					u.username as username
				FROM posts p
				JOIN users u ON p.user_id = u.id
				WHERE p.id = ?
				""",
                PostGetRes.class);

        query.setParameter(1, id);

        try {
            PostGetRes result = (PostGetRes) query.getSingleResult();
            return Optional.ofNullable(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
