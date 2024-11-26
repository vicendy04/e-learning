package com.myproject.elearning.service;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.dto.response.content.ContentListResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.content.ContentCreateMapper;
import com.myproject.elearning.mapper.content.ContentGetMapper;
import com.myproject.elearning.mapper.content.ContentListMapper;
import com.myproject.elearning.mapper.content.ContentUpdateMapper;
import com.myproject.elearning.repository.ContentRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class for managing contents.
 */
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;
    private final ContentGetMapper contentGetMapper;
    private final ContentCreateMapper contentCreateMapper;
    private final ContentUpdateMapper contentUpdateMapper;
    private final ContentListMapper contentListMapper;

    public ContentGetResponse createContent(ContentCreateRequest request) {
        Content content = contentCreateMapper.toEntity(request);
        Content savedContent = contentRepository.save(content);
        return contentGetMapper.toDto(savedContent);
    }

    public ContentGetResponse getContent(Long id) {
        Content content = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        return contentGetMapper.toDto(content);
    }

    @Transactional
    public ContentGetResponse updateContent(Long id, ContentUpdateRequest request) {
        Content currentContent = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        contentUpdateMapper.partialUpdate(currentContent, request);
        contentRepository.save(currentContent);
        return contentGetMapper.toDto(currentContent);
    }

    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        contentRepository.delete(content);
    }

    public PagedResponse<ContentGetResponse> getAllContents(Pageable pageable) {
        Page<Content> contents = contentRepository.findAllWithCourse(pageable);
        Page<ContentGetResponse> contentResponses = contents.map(contentGetMapper::toDto);
        return PagedResponse.from(contentResponses);
    }

    public PagedResponse<ContentListResponse> getContentsByCourseId(Long courseId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByCourseIdWithCourse(courseId, pageable);
        return PagedResponse.from(contents.map(contentListMapper::toDto));
    }

    @Transactional
    public ContentGetResponse addContentToCourse(Long courseId, ContentCreateRequest request) {
        Course courseOnlyId = courseRepository.getReferenceById(courseId);
        Content content = contentCreateMapper.toEntity(request);
        content.setCourse(courseOnlyId);
        contentRepository.save(content);
        return contentGetMapper.toDto(content);
    }

    public List<ContentListResponse> reorderContents(Long courseId, Map<Long, Integer> orderMapping) {
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
        return contentListMapper.toDto(contents);
    }

    @Transactional
    public void deleteContentsOfCourse(Long courseId) {
        Course course =
                courseRepository.findWithContentsById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        course.getContents().clear();
        courseRepository.save(course);
    }
}
