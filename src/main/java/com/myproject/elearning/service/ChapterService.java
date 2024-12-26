package com.myproject.elearning.service;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.ChapterMapper;
import com.myproject.elearning.repository.ChapterRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ChapterService {
    ChapterRepository chapterRepository;
    CourseRepository courseRepository;
    ChapterMapper chapterMapper;

    @Transactional
    public ChapterGetRes addChapter(Long courseId, ChapterCreateReq request) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new InvalidIdException("Course not found"));
        Chapter chapter = chapterMapper.toEntity(request);
        chapter.setCourse(course);
        if (chapter.getOrderIndex() == null) {
            List<Chapter> existingChapters = chapterRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
            chapter.setOrderIndex(existingChapters.size() + 1);
        }

        Chapter savedChapter = chapterRepository.save(chapter);
        return chapterMapper.toGetResponse(savedChapter);
    }

    public ChapterGetRes getChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdException("Chapter not found"));
        return chapterMapper.toGetResponse(chapter);
    }

    public PagedRes<ChapterGetRes> getChaptersByCourseId(Long courseId, Pageable pageable) {
        Page<Chapter> contents = chapterRepository.findByCourseIdWithCourse(courseId, pageable);
        return PagedRes.from(contents.map(chapterMapper::toGetResponse));
    }

    @Transactional
    public ChapterGetRes editChapter(Long id, ChapterUpdateReq request) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new InvalidIdException("Chapter not found"));
        chapterMapper.partialUpdate(chapter, request);
        Chapter updatedChapter = chapterRepository.save(chapter);
        return chapterMapper.toGetResponse(updatedChapter);
    }

    @Transactional
    public void deleteChapter(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new InvalidIdException("Chapter not found");
        }
        chapterRepository.deleteById(id);
    }

    @Transactional
    public void delChaptersByCourseId(Long courseId) {
        Course course =
                courseRepository.findWithChaptersById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        course.getChapters().clear();
        courseRepository.save(course);
    }
}
