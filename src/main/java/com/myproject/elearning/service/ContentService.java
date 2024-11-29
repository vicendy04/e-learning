package com.myproject.elearning.service;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.content.ContentCreateReq;
import com.myproject.elearning.dto.request.content.ContentUpdateReq;
import com.myproject.elearning.dto.response.content.ContentGetRes;
import com.myproject.elearning.dto.response.content.ContentListRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.ContentMapper;
import com.myproject.elearning.repository.ContentRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing contents.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ContentService {
    ContentRepository contentRepository;
    CourseRepository courseRepository;
    ContentMapper contentMapper;

    public ContentGetRes addContent(ContentCreateReq request) {
        Content content = contentMapper.toEntity(request);
        Content savedContent = contentRepository.save(content);
        return contentMapper.toGetResponse(savedContent);
    }

    public ContentGetRes getContent(Long id) {
        Content content = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        return contentMapper.toGetResponse(content);
    }

    @Transactional
    public ContentGetRes editContent(Long id, ContentUpdateReq request) {
        Content currentContent = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        contentMapper.partialUpdate(currentContent, request);
        contentRepository.save(currentContent);
        return contentMapper.toGetResponse(currentContent);
    }

    public void delContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        contentRepository.delete(content);
    }

    public PagedRes<ContentGetRes> getContents(Pageable pageable) {
        Page<Content> contents = contentRepository.findAllWithCourse(pageable);
        Page<ContentGetRes> contentResponses = contents.map(contentMapper::toGetResponse);
        return PagedRes.from(contentResponses);
    }

    public PagedRes<ContentListRes> getContentsByCourseId(Long courseId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByCourseIdWithCourse(courseId, pageable);
        return PagedRes.from(contents.map(contentMapper::toListResponse));
    }

    @Transactional
    public ContentGetRes addContentToCourse(Long courseId, ContentCreateReq request) {
        Course courseOnlyId = courseRepository.getReferenceById(courseId);
        Content content = contentMapper.toEntity(request);
        content.setCourse(courseOnlyId);
        contentRepository.save(content);
        return contentMapper.toGetResponse(content);
    }

    public List<ContentListRes> reorderContents(Long courseId, Map<Long, Integer> orderMapping) {
        Course course =
                courseRepository.findWithContentsById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        List<Content> contents = course.getContents();
        contents.forEach(content -> {
            Integer newOrder = orderMapping.get(content.getId());
            if (newOrder != null) {
                content.setOrderIndex(newOrder);
            }
        });
        contentRepository.saveAll(contents);
        return contentMapper.toListResponse(contents);
    }

    @Transactional
    public void delContentsOfCourse(Long courseId) {
        Course course =
                courseRepository.findWithContentsById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        course.getContents().clear();
        courseRepository.save(course);
    }
}
