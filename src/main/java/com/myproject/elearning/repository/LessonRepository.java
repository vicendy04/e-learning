package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllByChapterId(Long chapterId);

    @Query("""
				SELECT l.chapter.course.instructor.id
				FROM Lesson l
				WHERE l.id = :lessonId
			""")
    Optional<Long> findInstructorIdByChapterId(Long lessonId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.chapter WHERE l.id = :id")
    Optional<Lesson> findByIdWithChapter(Long id);

    Optional<Lesson> findByIdAndChapterCourseInstructorId(Long id, Long instructorId);

    boolean existsByIdAndChapterCourseInstructorId(Long id, Long instructorId);

    List<Lesson> findByChapterIdOrderByOrderIndexAsc(Long chapterId);
}
