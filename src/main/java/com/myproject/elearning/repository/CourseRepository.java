package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Course} entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    default Course getReferenceIfExists(Long id) {
        if (!existsById(id)) {
            throw new InvalidIdException("Entity with ID " + id + " not found");
        }
        return getReferenceById(id);
    }

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id =:courseId")
    int countEnrollmentsByCourseId(@Param("courseId") Long courseId);

    @EntityGraph(attributePaths = "chapters")
    Optional<Course> findWithChaptersById(Long id);

    @EntityGraph(attributePaths = "instructor")
    Optional<Course> findWithInstructorById(Long id);

    @Query("SELECT c.id FROM Course c WHERE c.instructor.id = :instructorId")
    Set<Long> findCourseIdsByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT c, COUNT(e) FROM Course c LEFT JOIN c.enrollments e GROUP BY c")
    Page<Object[]> findAllWithEnrollmentCount(Specification<Course> spec, Pageable pageable);

    // Tối ưu cho tìm kiếm
    // Specification
    @Query("SELECT c FROM Course c " + "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);

    @Query(
            """
					SELECT c.id as id,
						c.price as price,
						c.instructor.id as instructorId
					FROM Course c
					WHERE c.id = :courseId
					""")
    Optional<CourseForValidDiscount> findCourseWithInstructor(@Param("courseId") Long courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Course c WHERE c.id = :id")
    void deleteByCourseId(Long id);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    @Modifying
    @Query("UPDATE Course c SET c.enrolledCount = c.enrolledCount + 1 WHERE c.id = :courseId")
    void incrementEnrollmentCount(@Param("courseId") Long courseId);

    @Modifying
    @Query("UPDATE Course c SET c.enrolledCount = c.enrolledCount - 1 WHERE c.id = :courseId")
    void decrementEnrollmentCount(@Param("courseId") Long courseId);

    interface CourseForValidDiscount {
        Long getId();

        BigDecimal getPrice();

        Long getInstructorId();
    }
}
