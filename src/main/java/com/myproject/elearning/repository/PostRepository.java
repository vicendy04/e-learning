package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Page<Post> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
    int deleteByIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT p.user.id FROM Post p WHERE p.id = :postId")
    Optional<Long> findUserIdByPostId(@Param("postId") Long postId);
}
