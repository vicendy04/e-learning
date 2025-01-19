package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Course} entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    @EntityGraph(attributePaths = "topic")
    Page<Course> findAllByInstructorId(Long instructorId, Pageable pageable);

    @EntityGraph(attributePaths = "topic")
    Page<Course> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = "chapters")
    Optional<Course> findWithChaptersById(Long id);

    @EntityGraph(attributePaths = {"instructor", "topic"})
    Optional<Course> findWithInstructorAndTopicById(Long id);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id =:courseId")
    int countEnrollmentsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT c.id FROM Course c WHERE c.instructor.id = :instructorId")
    Set<Long> findCourseIdsByInstructorId(@Param("instructorId") Long instructorId);

    @Query(
            """
					SELECT c.id as id,
						c.price as price,
						c.instructor.id as instructorId
					FROM Course c
					WHERE c.id = :courseId
					""")
    Optional<CourseForValidDiscount> findCourseWithInstructor(@Param("courseId") Long courseId);

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
    Page<Course> findByEnrollmentsUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c.instructor.id FROM Course c WHERE c.id = :courseId")
    Optional<Long> findInstructorIdByCourseId(@Param("courseId") Long courseId);

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
