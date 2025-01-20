package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.dto.response.post.PostGetRes;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
    int deleteByIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT p.user.id FROM Post p WHERE p.id = :postId")
    Optional<Long> findUserIdById(@Param("postId") Long postId);

    @Query(
            """
			SELECT new com.myproject.elearning.dto.response.post.PostGetRes(p.id, p.content, u.id, u.username)
			FROM Post p
			JOIN p.user u
			WHERE p.id = :id
			""")
    Optional<PostGetRes> findPostGetResById(@Param("id") Long id);

    @Query(
            """
					SELECT new com.myproject.elearning.dto.response.post.PostGetRes(p.id, p.content, u.id, u.username)
					FROM Post p
					JOIN p.user u
					WHERE p.id IN :ids
					""")
    List<PostGetRes> findPostGetResByIds(Set<Long> ids);
}
