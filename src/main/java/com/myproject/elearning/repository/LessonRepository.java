package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.chapter WHERE l.id = :id")
    Optional<Lesson> findByIdWithChapter(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.id = :id")
    void deleteByLessonId(@Param("id") Long id);

    List<Lesson> findAllByChapterId(Long chapterId);
}
