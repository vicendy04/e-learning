package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Chapter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>, JpaSpecificationExecutor<Chapter> {
    List<Chapter> findByCourseId(@Param("courseId") Long courseId);

    List<Chapter> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    @EntityGraph(attributePaths = "lessons")
    Optional<Chapter> findWithLessonsById(Long id);

    @Query("""
			SELECT c.course.instructor.id
			FROM Chapter c
			WHERE c.id = :chapterId
			""")
    Optional<Long> findInstructorIdById(Long chapterId);

    @Query("SELECT c FROM Chapter c " + "LEFT JOIN FETCH c.lessons l "
            + "WHERE c.course.id = :courseId "
            + "ORDER BY c.orderIndex ASC, l.orderIndex ASC")
    List<Chapter> findAllWithLessonsByCourseId(@Param("courseId") Long courseId);
}
