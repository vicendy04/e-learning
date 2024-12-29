package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Chapter;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>, JpaSpecificationExecutor<Chapter> {
    List<Chapter> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    @Query("SELECT c FROM Chapter c WHERE c.course.id = :courseId")
    Page<Chapter> findByCourseIdWithCourse(@Param("courseId") Long courseId, Pageable pageable);

    boolean existsByIdAndCourseInstructorId(Long chapterId, Long instructorIdkA);
}
