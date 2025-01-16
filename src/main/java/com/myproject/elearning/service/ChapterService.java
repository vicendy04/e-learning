package com.myproject.elearning.service;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.mapper.ChapterMapper;
import com.myproject.elearning.repository.ChapterRepository;
import com.myproject.elearning.repository.CourseRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ChapterService {
    ChapterRepository chapterRepository;
    CourseRepository courseRepository;
    ChapterMapper chapterMapper;

    @Transactional
    public ChapterGetRes addChapter(Long courseId, ChapterCreateReq request) {
        Chapter chapter = chapterMapper.toEntity(request);
        List<Chapter> existingChapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        chapter.setOrderIndex(existingChapters.size() + 1);
        chapter.setCourse(courseRepository.getReferenceById(courseId));
        Chapter savedChapter = chapterRepository.save(chapter);
        return chapterMapper.toGetRes(savedChapter);
    }

    public ChapterGetRes getChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Chapter not found"));
        return chapterMapper.toGetRes(chapter);
    }

    public PagedRes<ChapterGetRes> getChaptersByCourseId(Long courseId, Pageable pageable) {
        Page<Chapter> contents = chapterRepository.findByCourseIdWithCourse(courseId, pageable);
        return PagedRes.from(contents.map(chapterMapper::toGetRes));
    }

    @Transactional
    public ChapterGetRes editChapter(Long id, ChapterUpdateReq request) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdEx("Chapter not found"));
        chapterMapper.partialUpdate(chapter, request);
        Chapter updatedChapter = chapterRepository.save(chapter);
        return chapterMapper.toGetRes(updatedChapter);
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
}
