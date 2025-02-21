package com.myproject.elearning.repository.impl;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <a href="https://mkyong.com/spring/spring-jdbctemplate-batchupdate-example/">...</a>
 * <a href="https://medium.tuanh.net/bulk-insert-in-spring-boot-a-comprehensive-guide-ad729e511b70">...</a>
 */
@Slf4j
@Repository
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {
    private static final int BATCH_SIZE = 100;
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public PostLikeRepositoryCustomImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public int[][] bulkInsertLikes(Set<PostLikeData> likes) {
        String sql = "INSERT IGNORE INTO post_likes (post_id, user_id) VALUES (?, ?)";
        return jdbcTemplate.batchUpdate(sql, likes, BATCH_SIZE, (ps, data) -> {
            ps.setLong(1, data.postId());
            ps.setLong(2, data.userId());
        });
    }

    @Transactional
    @Override
    public int bulkDeleteLikes(Set<PostLikeData> unlikes) {
        StringBuilder sql = new StringBuilder("DELETE FROM post_likes WHERE (post_id, user_id) IN ");

        String valuePlaceholders = unlikes.stream().map(pass -> "(?,?)").collect(Collectors.joining(",", "(", ")"));
        log.info(valuePlaceholders);

        sql.append(valuePlaceholders);

        Object[] params = unlikes.stream()
                .flatMap(like -> Stream.of(like.postId(), like.userId()))
                .toArray();

        return jdbcTemplate.update(sql.toString(), params);
    }

    @Override
    public Long countById(Long postId) {
        Query query = entityManager.createNativeQuery(
                """
						SELECT COUNT(*)
						FROM post_likes pl
						WHERE pl.post_id = ?
						""");

        query.setParameter(1, postId);

        return (Long) query.getSingleResult();
    }
}
