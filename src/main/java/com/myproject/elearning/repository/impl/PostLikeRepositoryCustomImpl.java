package com.myproject.elearning.repository.impl;

import com.myproject.elearning.dto.request.post.PostLikeData;
import com.myproject.elearning.repository.PostLikeRepositoryCustom;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    private static final int BATCH_SIZE = 100;

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
    public int[][] bulkDeleteLikes(Set<PostLikeData> unlikes) {
        String sql = "DELETE FROM post_likes WHERE post_id = ? AND user_id = ?";
        return jdbcTemplate.batchUpdate(sql, unlikes, BATCH_SIZE, (ps, data) -> {
            ps.setLong(1, data.postId());
            ps.setLong(2, data.userId());
        });
    }
}
