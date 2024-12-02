package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Review;
import com.myproject.elearning.dto.response.review.ReviewCourseRes;
import com.myproject.elearning.dto.response.review.ReviewUserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(
            """
			SELECT new com.myproject.elearning.dto.response.review.ReviewCourseRes(
				r.id, r.rating, r.comment,
				r.createdAt, r.updatedAt,
				u.id, u.username
			)
			FROM Review r
			LEFT JOIN r.user u
			LEFT JOIN r.course c
			WHERE c.id = :courseId
			""")
    Page<ReviewCourseRes> findAllByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    @Query(
            """
			SELECT new com.myproject.elearning.dto.response.review.ReviewUserRes(
				r.id, r.rating, r.comment,
				r.createdAt, r.updatedAt,
				c.id, c.title
			)
			FROM Review r
			LEFT JOIN r.course c
			LEFT JOIN r.user u
			WHERE u.id = :userId
			""")
    Page<ReviewUserRes> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double getAverageRatingByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.id = :id")
    int deleteByReviewId(@Param("id") Long reviewId);
}
