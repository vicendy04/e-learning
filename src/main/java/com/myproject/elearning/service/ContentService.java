package com.myproject.elearning.service;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.dto.response.content.ContentListResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.ContentMapper;
import com.myproject.elearning.repository.ContentRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing contents.
 */
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;
    private final ContentMapper contentMapper;

    public ContentGetResponse createContent(ContentCreateRequest request) {
        Content content = contentMapper.toEntity(request);
        Content savedContent = contentRepository.save(content);
        return contentMapper.toGetResponse(savedContent);
    }

    public ContentGetResponse getContent(Long id) {
        Content content = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        return contentMapper.toGetResponse(content);
    }

    @Transactional
    public ContentGetResponse updateContent(Long id, ContentUpdateRequest request) {
        Content currentContent = contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
        contentMapper.partialUpdate(currentContent, request);
        contentRepository.save(currentContent);
        return contentMapper.toGetResponse(currentContent);
    }

    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        contentRepository.delete(content);
    }

    public PagedResponse<ContentGetResponse> getAllContents(Pageable pageable) {
        Page<Content> contents = contentRepository.findAllWithCourse(pageable);
        Page<ContentGetResponse> contentResponses = contents.map(contentMapper::toGetResponse);
        return PagedResponse.from(contentResponses);
    }

    public PagedResponse<ContentListResponse> getContentsByCourseId(Long courseId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByCourseIdWithCourse(courseId, pageable);
        return PagedResponse.from(contents.map(contentMapper::toListResponse));
    }

    @Transactional
    public ContentGetResponse addContentToCourse(Long courseId, ContentCreateRequest request) {
        Course courseOnlyId = courseRepository.getReferenceById(courseId);
        Content content = contentMapper.toEntity(request);
        content.setCourse(courseOnlyId);
        contentRepository.save(content);
        return contentMapper.toGetResponse(content);
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
        return contentMapper.toListResponse(contents);
    }

    @Transactional
    public void deleteContentsOfCourse(Long courseId) {
        Course course =
                courseRepository.findWithContentsById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        course.getContents().clear();
        courseRepository.save(course);
    }
}
