package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.ChapterMapper.CHAPTER_MAPPER;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterRes;
import com.myproject.elearning.dto.response.chapter.ExpandedChapterRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.ChapterRepository;
import com.myproject.elearning.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ChapterService {
    ChapterRepository chapterRepository;
    CourseRepository courseRepository;

    @Transactional
    public ChapterRes addChapter(Long courseId, ChapterCreateReq request) {
        Chapter chapter = CHAPTER_MAPPER.toEntity(request);
        List<Chapter> existingChapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        chapter.setOrderIndex(existingChapters.size() + 1);
        chapter.setCourse(courseRepository.getReferenceById(courseId));
        Chapter savedChapter = chapterRepository.save(chapter);
        return CHAPTER_MAPPER.toRes(savedChapter);
    }

    @Transactional(readOnly = true)
    public ChapterRes getChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Chapter not found"));
        return CHAPTER_MAPPER.toRes(chapter);
    }

    public List<ChapterRes> getChaptersByCourseId(Long courseId) {
        List<Chapter> contents = chapterRepository.findByCourseId(courseId);
        return contents.stream().map(CHAPTER_MAPPER::toRes).collect(Collectors.toList());
    }

    @Transactional
    public ChapterRes editChapter(Long id, ChapterUpdateReq request) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Chapter not found"));
        CHAPTER_MAPPER.partialUpdate(chapter, request);
        Chapter updatedChapter = chapterRepository.save(chapter);
        return CHAPTER_MAPPER.toRes(updatedChapter);
    }

    @Transactional
    public void deleteChapter(Long id) {
        chapterRepository.deleteById(id);
    }

    @Transactional
    public void delChaptersByCourseId(Long courseId) {
        Course course = courseRepository.findWithChaptersById(courseId).orElseThrow(() -> new InvalidIdEx(courseId));
        course.getChapters().clear();
        courseRepository.save(course);
    }

    public List<ExpandedChapterRes> getExpandedChapters(Long courseId) {
        List<Chapter> chapters = chapterRepository.findAllWithLessonsByCourseId(courseId);
        return chapters.stream().map(CHAPTER_MAPPER::toExpandedChapterRes).collect(Collectors.toList());
    }
}
