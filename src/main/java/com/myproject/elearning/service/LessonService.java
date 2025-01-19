package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.LessonMapper.LESSON_MAPPER;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.ChapterRepository;
import com.myproject.elearning.repository.LessonRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonService {
    LessonRepository lessonRepository;
    ChapterRepository chapterRepository;

    @Transactional
    public LessonRes addLessonToChapter(Long chapterId, LessonCreateReq request) {
        Lesson lesson = LESSON_MAPPER.toEntity(request);
        List<Lesson> existingLessons = lessonRepository.findByChapterIdOrderByOrderIndexAsc(chapterId);
        lesson.setOrderIndex(existingLessons.size() + 1);
        Chapter chapterRef = chapterRepository.getReferenceById(chapterId);
        lesson.setChapter(chapterRef);
        return LESSON_MAPPER.toRes(lessonRepository.save(lesson));
    }

    public LessonRes getLesson(Long id) {
        return LESSON_MAPPER.toRes(lessonRepository.findByIdWithChapter(id).orElseThrow(() -> new InvalidIdEx(id)));
    }

    @Transactional
    public LessonRes editLesson(Long id, LessonUpdateReq request) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Bài học không tồn tại"));
        LESSON_MAPPER.partialUpdate(lesson, request);
        return LESSON_MAPPER.toRes(lessonRepository.save(lesson));
    }

    @Transactional
    public void delLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    public List<LessonRes> getLessonsByChapterId(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findAllByChapterId(chapterId);
        return lessons.stream().map(LESSON_MAPPER::toRes).collect(Collectors.toList());
    }

    @Transactional
    public void delLessonsByChapterId(Long chapterId) {
        Chapter chapter =
                chapterRepository.findWithLessonsById(chapterId).orElseThrow(() -> new InvalidIdEx(chapterId));
        chapter.getLessons().clear();
        chapterRepository.save(chapter);
    }
}
