package com.myproject.elearning.service;

import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.*;
import com.myproject.elearning.security.SecurityUtils;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ResourceAccessService {
    EnrollmentRepository enrollmentRepository;
    CourseRepository courseRepository;
    PostRepository postRepository;
    ReviewRepository reviewRepository;
    DiscountRepository discountRepository;
    ChapterRepository chapterRepository;
    LessonRepository lessonRepository;

    private Long getCurrentUserId() {
        return SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
    }

    private boolean isOwner(Long owner) {
        return Objects.equals(getCurrentUserId(), owner);
    }

    public boolean isEnrollmentOwner(Long enrollmentId) {
        Long owner = enrollmentRepository
                .findUserIdByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new InvalidIdException(enrollmentId));
        return isOwner(owner);
    }

    public boolean isDiscountOwner(Long disCountId) {
        Long owner = discountRepository
                .findInstructorIdByCourseId(disCountId)
                .orElseThrow(() -> new InvalidIdException(disCountId));
        return isOwner(owner);
    }

    public boolean isCourseOwner(Long courseId) {
        Long owner = courseRepository
                .findInstructorIdByCourseId(courseId)
                .orElseThrow(() -> new InvalidIdException(courseId));
        return isOwner(owner);
    }

    public boolean isChapterOwner(Long chapterId) {
        Long owner = chapterRepository
                .findInstructorIdByChapterId(chapterId)
                .orElseThrow(() -> new InvalidIdException(chapterId));
        return isOwner(owner);
    }

    public boolean isLessonOwner(Long lessonId) {
        Long owner = lessonRepository
                .findInstructorIdByChapterId(lessonId)
                .orElseThrow(() -> new InvalidIdException(lessonId));
        return isOwner(owner);
    }

    public boolean isUserOwner(Long userId) {
        return isOwner(userId);
    }

    public boolean isPostOwner(Long postId) {
        Long owner = postRepository.findUserIdByPostId(postId).orElseThrow(() -> new InvalidIdException(postId));
        return isOwner(owner);
    }

    public boolean isReviewOwner(Long reviewId) {
        Long owner =
                reviewRepository.findUserIdByReviewId(reviewId).orElseThrow(() -> new InvalidIdException(reviewId));
        return isOwner(owner);
    }
}
