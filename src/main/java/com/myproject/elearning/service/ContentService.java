package com.myproject.elearning.service;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.ContentListResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.ContentRepository;
import com.myproject.elearning.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing contents.
 */
@Service
public class ContentService {
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;

    public ContentService(ContentRepository contentRepository, CourseRepository courseRepository) {
        this.contentRepository = contentRepository;
        this.courseRepository = courseRepository;
    }

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content getContent(Long id) {
        return contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
    }

    @Transactional
    public Content updateContent(Content content) {
        Content currentContent =
                contentRepository.findById(content.getId()).orElseThrow(() -> new InvalidIdException(content.getId()));
        if (content.getCourse() != null && content.getCourse().getId() != null) {
            Course course = courseRepository
                    .findById(content.getCourse().getId())
                    .orElseThrow(
                            () -> new InvalidIdException(content.getCourse().getId()));
            content.setCourse(course);
        }
        currentContent.setTitle(content.getTitle());
        currentContent.setOrderIndex(content.getOrderIndex());
        currentContent.setContentType(content.getContentType());
        currentContent.setStatus(content.getStatus());
        currentContent.setCourse(content.getCourse());
        return contentRepository.save(currentContent);
    }

    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        contentRepository.delete(content);
    }

    public PagedResponse<Content> getAllContents(Pageable pageable) {
        Page<Content> contents = contentRepository.findAll(pageable);
        return PagedResponse.from(contents);
    }

    public PagedResponse<ContentListResponse> getContentsByCourseId(Long courseId, Pageable pageable) {
        Page<Content> contents = contentRepository.findByCourseId(courseId, pageable);
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
