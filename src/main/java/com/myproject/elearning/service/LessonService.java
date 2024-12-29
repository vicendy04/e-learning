package com.myproject.elearning.service;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonGetRes;
import com.myproject.elearning.dto.response.lesson.LessonListRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.LessonMapper;
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
    LessonMapper lessonMapper;

    @Transactional
    public LessonGetRes addLessonToChapter(Long chapterId, LessonCreateReq request, Long instructorId) {
        // Todo: Check the behavior of Spring Data JPA
        boolean exists = chapterRepository.existsByIdAndCourseInstructorId(chapterId, instructorId);
        if (!exists) {
            throw new InvalidIdException("Chapter không tồn tại hoặc không thuộc về bạn");
        }

        Chapter chapterOnlyId = chapterRepository.getReferenceById(chapterId);
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setChapter(chapterOnlyId);
        return lessonMapper.toGetResponse(lessonRepository.save(lesson));
    }

    public LessonGetRes getLesson(Long id) {
        return lessonMapper.toGetResponse(
                lessonRepository.findByIdWithChapter(id).orElseThrow(() -> new InvalidIdException(id)));
    }

    @Transactional
    public LessonGetRes editLesson(Long id, LessonUpdateReq request, Long instructorId) {
        // Todo: Check the behavior of Spring Data JPA
        // Todo: n + 1 problem
        Lesson lesson = lessonRepository
                .findByIdAndChapterCourseInstructorId(id, instructorId)
                .orElseThrow(() -> new InvalidIdException("Bài học không tồn tại hoặc không thuộc về bạn"));

        lessonMapper.partialUpdate(lesson, request);
        return lessonMapper.toGetResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void delLesson(Long id, Long instructorId) {
        if (!lessonRepository.existsByIdAndChapterCourseInstructorId(id, instructorId)) {
            throw new InvalidIdException("Bài học không tồn tại hoặc không thuộc về bạn");
        }
        lessonRepository.deleteByLessonId(id);
    }

    public List<LessonListRes> getLessonsByChapterId(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findAllByChapterId(chapterId);
        return lessons.stream().map(lessonMapper::toLessonListRes).collect(Collectors.toList());
    }
}
