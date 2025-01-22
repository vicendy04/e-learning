package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Course} entity.
 * <a href="https://vladmihalcea.com/join-fetch-pagination-spring/">...</a>
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    @EntityGraph(attributePaths = "topic")
    Page<Course> findAllByInstructorId(Long instructorId, Pageable pageable);

    @Query(
            value = """
					select c.id
					from Course c
					where c.topic.id in :topicIds
					""",
            countQuery = """
					select count(c)
					from Course c
					where c.topic.id in :topicIds
					""")
    Page<Long> findIdsByTopicIds(@Param("topicIds") Set<Long> topicIds, Pageable pageable);

    @Query(
            """
			SELECT new com.myproject.elearning.dto.CourseData(c.id, c.title,
			c.description, c.duration,
			c.price, c.level, c.thumbnailUrl,
			c.enrolledCount, c.reviewCount,
			c.instructor.id, c.instructor.firstName, c.instructor.lastName,
			c.instructor.imageUrl,
			c.topic.id, c.topic.name)
			from Course c
			left join c.topic
			left join c.instructor
			where c.id in :courseIds
			""")
    List<CourseData> findAllWithTopicBy(@Param("courseIds") List<Long> courseIds);

    @Query(
            value =
                    """
			SELECT new com.myproject.elearning.dto.CourseData(
				c.id, c.title, c.description, c.duration, c.price, c.level,
				c.thumbnailUrl, c.enrolledCount, c.reviewCount,
				c.instructor.id, c.instructor.firstName, c.instructor.lastName, c.instructor.imageUrl,
				c.topic.id, c.topic.name
			)
			FROM Course c
			LEFT JOIN c.topic
			LEFT JOIN c.instructor
			""",
            countQuery = """
					SELECT count(c) FROM Course c
					""")
    Page<CourseData> findAllWithTopic(Pageable pageable);

    @EntityGraph(attributePaths = "chapters")
    Optional<Course> findWithChaptersById(Long id);

    @EntityGraph(attributePaths = {"instructor", "topic"})
    Optional<Course> findWithInstructorAndTopicById(Long id);

    @Query("SELECT c.id FROM Course c WHERE c.instructor.id = :instructorId")
    Set<Long> findIdsByInstructorId(@Param("instructorId") Long instructorId);

    @Query(
            """
					SELECT c.id as id,
						c.price as price,
						c.instructor.id as instructorId
					FROM Course c
					WHERE c.id = :courseId
					""")
    Optional<CourseForValidDiscount> findCourseDetailsForDiscount(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Course c SET c.reviewCount = c.reviewCount + 1 WHERE c.id = :courseId")
    void incrementReviewCount(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Course c SET c.reviewCount = c.reviewCount - 1 WHERE c.id = :courseId")
    void decrementReviewCount(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Course c SET c.enrolledCount = c.enrolledCount + 1 WHERE c.id = :courseId")
    void incrementEnrollmentCount(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Course c SET c.enrolledCount = c.enrolledCount - 1 WHERE c.id = :courseId")
    void decrementEnrollmentCount(@Param("courseId") Long courseId);

    @Query(value = "SELECT c FROM Course c JOIN c.enrollments e WHERE e.user.id = :userId")
    Page<Course> findAllByEnrollmentUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c.instructor.id FROM Course c WHERE c.id = :courseId")
    Optional<Long> findInstructorIdById(@Param("courseId") Long courseId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double getAverageRatingById(@Param("courseId") Long courseId);

    default Course getReferenceIfExists(Long id) {
        if (!existsById(id)) {
            throw new InvalidIdEx("Entity with ID " + id + " not found");
        }
        return getReferenceById(id);
    }

    interface CourseForValidDiscount {
        Long getId();

        BigDecimal getPrice();

        Long getInstructorId();
    }
}
