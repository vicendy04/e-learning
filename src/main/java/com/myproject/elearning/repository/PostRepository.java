package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.repository.custom.PostRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Page<Post> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Post p WHERE p.id = :id")
    void deleteByPostId(@Param("id") Long id);
}
