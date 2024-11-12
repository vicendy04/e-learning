package com.myproject.elearning.service;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.ContentUpdateInput;
import com.myproject.elearning.dto.response.ContentListResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.ContentRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content getContent(Long id) {
        //        return contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        return contentRepository.findByIdWithCourse(id).orElseThrow(() -> new InvalidIdException(id));
    }

    @Transactional
    public Content updateContent(ContentUpdateInput contentUpdateInput) {
        //        Content currentContent = contentRepository.findById(contentUpdateDTO.getId()).orElseThrow(() -> new
        // InvalidIdException(contentUpdateDTO.getId()));
        Content currentContent = contentRepository
                .findByIdWithCourse(contentUpdateInput.getId())
                .orElseThrow(() -> new InvalidIdException(contentUpdateInput.getId()));
        if (!Objects.equals(
                contentUpdateInput.getCourseId(), currentContent.getCourse().getId())) {
            Course course = courseRepository
                    .findById(contentUpdateInput.getCourseId())
                    .orElseThrow(() -> new InvalidIdException(contentUpdateInput.getCourseId()));
            currentContent.setCourse(course);
        }
        currentContent.setTitle(contentUpdateInput.getTitle());
        currentContent.setOrderIndex(contentUpdateInput.getOrderIndex());
        currentContent.setContentType(contentUpdateInput.getContentType());
        currentContent.setStatus(contentUpdateInput.getStatus());
        return contentRepository.save(currentContent);
    }

    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        contentRepository.delete(content);
    }

    public PagedResponse<Content> getAllContents(Pageable pageable) {
        //        Page<Content> contents = contentRepository.findAll(pageable);
        Page<Content> contents = contentRepository.findAllWithCourse(pageable);
        return PagedResponse.from(contents);
    }

    public PagedResponse<ContentListResponse> getContentsByCourseId(Long courseId, Pageable pageable) {
        //        Page<Content> contents = contentRepository.findByCourseId(courseId, pageable);
        Page<Content> contents = contentRepository.findByCourseIdWithCourse(courseId, pageable);
        Page<ContentListResponse> contentListResponses = contents.map(ContentListResponse::new);
        return PagedResponse.from(contentListResponses);
    }

    public Content addContentToCourse(Long courseId, Content content) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        content.setCourse(course);
        return contentRepository.save(content);
    }

    public List<Content> reorderContents(Long courseId, Map<Long, Integer> orderMapping) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        List<Content> contents = course.getContents();
        contents.forEach(content -> {
            Integer newOrder = orderMapping.get(content.getId());
            if (newOrder != null) {
                content.setOrderIndex(newOrder);
            }
        });
        return contentRepository.saveAll(contents);
    }

    @Transactional
    public void deleteContentsOfCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new InvalidIdException(courseId));
        course.getContents().clear();
        courseRepository.save(course);
    }
}
