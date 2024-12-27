package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.repository.custom.EnrollmentRepositoryCustom;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, EnrollmentRepositoryCustom {
    boolean existsByUserIdAndCourseId(Long id, Long courseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId")
    void deleteByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e " + "LEFT JOIN FETCH e.user u "
            + "LEFT JOIN FETCH e.course c "
            + "WHERE u.id= :id")
    Page<Enrollment> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT e FROM Enrollment e " + "LEFT JOIN FETCH e.user u "
            + "LEFT JOIN FETCH e.course c "
            + "WHERE c.id= :courseId")
    Page<Enrollment> findAllByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e " + "LEFT JOIN FETCH e.user u "
            + "LEFT JOIN FETCH e.course c "
            + "WHERE e.id = :enrollmentId")
    Optional<Enrollment> findByIdWithDetails(@Param("enrollmentId") Long enrollmentId);

    // specification
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.status = :status")
    Page<Enrollment> findByUserIdAndStatus(
            @Param("userId") Long userId, @Param("status") Enrollment.EnrollmentStatus status, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    Page<Enrollment> findByCourseIdAndStatus(
            @Param("courseId") Long courseId, @Param("status") Enrollment.EnrollmentStatus status, Pageable pageable);
}
