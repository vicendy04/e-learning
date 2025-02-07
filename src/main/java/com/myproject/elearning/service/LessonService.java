package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.LessonMapper.LESSON_MAPPER;
import static com.myproject.elearning.security.SecurityUtils.getCurrentUserId;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonContentRes;
import com.myproject.elearning.dto.response.lesson.LessonRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.ChapterRepository;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.repository.LessonRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {
    LessonRepository lessonRepository;
    ChapterRepository chapterRepository;
    EnrollmentRepository enrollmentRepository;

    @Transactional
    public LessonRes addLessonToChapter(Long chapterId, LessonCreateReq request) {
        Lesson lesson = LESSON_MAPPER.toEntity(request);
        List<Lesson> existingLessons = lessonRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        lesson.setOrderIndex(existingLessons.size() + 1);
        Chapter chapterRef = chapterRepository.getReferenceById(chapterId);
        lesson.setChapter(chapterRef);
        return LESSON_MAPPER.toRes(lessonRepository.save(lesson));
    }

    @Transactional(readOnly = true)
    public LessonContentRes getLessonContent(Long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Lesson not found"));
        if (!lesson.getIsFreePreview()) {
            Long userId = getCurrentUserId();
            Long courseId = lessonRepository.findCourseIdById(id);
            boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
            if (!isEnrolled) {
                throw new AccessDeniedException("User is not enrolled in this course");
            }
        }
        return LESSON_MAPPER.toContentRes(lesson);
    }

    @Transactional
    public LessonRes editLesson(Long id, LessonUpdateReq request) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Lesson not found"));
        LESSON_MAPPER.partialUpdate(lesson, request);
        return LESSON_MAPPER.toRes(lessonRepository.save(lesson));
    }

    @Transactional
    public void delLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    public List<LessonRes> getLessonsByChapter(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findAllByChapterId(chapterId);
        return lessons.stream().map(LESSON_MAPPER::toRes).toList();
    }

    @Transactional
    public void delLessonsByChapterId(Long chapterId) {
        Chapter chapter =
                chapterRepository.findWithLessonsById(chapterId).orElseThrow(() -> new InvalidIdEx(chapterId));
        chapter.getLessons().clear();
        chapterRepository.save(chapter);
    }
}
