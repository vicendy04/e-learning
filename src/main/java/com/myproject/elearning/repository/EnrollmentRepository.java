package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Enrollment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    int countEnrollmentByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT e.user.id FROM Enrollment e WHERE e.id = :enrollmentId")
    Optional<Long> findInstructorIdById(@Param("enrollmentId") Long enrollmentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId")
    void deleteByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e " + "LEFT JOIN FETCH e.user u " + "LEFT JOIN FETCH e.course c "
            + "WHERE e.user.id = :id")
    Page<Enrollment> getPagedEnrollmentsByUserId(Long id, Pageable pageable);

    @EntityGraph(attributePaths = "course")
    Optional<Enrollment> findWithCourseById(@Param("enrollmentId") Long enrollmentId);
}
