package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Content;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Content} entity.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.course WHERE c.id = :id")
    Optional<Content> findByIdWithCourse(Long id);

    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.course WHERE c.course.id = :courseId")
    Page<Content> findByCourseIdWithCourse(@Param("courseId") Long courseId, Pageable pageable);

    // don't use distinct
    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.course")
    Page<Content> findAllWithCourse(Pageable pageable);

    //  specification
    @Query("SELECT c FROM Content c WHERE c.course.id = :courseId AND c.status = :status")
    List<Content> findByCourseIdAndStatus(
            @Param("courseId") Long courseId, @Param("status") Content.ContentStatus status);

    // don't use join fetch
    @Query("SELECT COUNT(c) FROM Content c WHERE c.course.id = :courseId")
    int countByCourseId(@Param("courseId") Long courseId);

    Page<Content> findByCourseId(Long courseId, Pageable pageable);
}
