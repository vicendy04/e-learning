package com.myproject.elearning.service;

import com.myproject.elearning.constant.ResourceType;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.*;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.redis.RedisResourceAccessService;
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
    RedisResourceAccessService redisResourceAccess;

    private Long getCurrentUserId() {
        return SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
    }

    private boolean isOwner(Long owner) {
        return Objects.equals(getCurrentUserId(), owner);
    }

    public boolean isCourseOwner(Long courseId) {
        Long userId = getCurrentUserId();
        // try cache first
        Boolean cached = redisResourceAccess.getCachedOwnership(ResourceType.COURSE, courseId, userId);
        if (cached != null) {
            return cached;
        }
        // cache miss, query db
        boolean isOwner = courseRepository
                .findInstructorIdByCourseId(courseId)
                .map(ownerId -> Objects.equals(userId, ownerId)) // if present
                .orElseGet(() -> false); // if empty
        // cache
        redisResourceAccess.setCachedOwnership(ResourceType.COURSE, courseId, userId, isOwner);
        return isOwner;
    }

    public boolean isChapterOwner(Long chapterId) {
        Long userId = getCurrentUserId();
        Boolean cached = redisResourceAccess.getCachedOwnership(ResourceType.CHAPTER, chapterId, userId);
        if (cached != null) {
            return cached;
        }
        boolean isOwner = chapterRepository
                .findInstructorIdByChapterId(chapterId)
                .map(ownerId -> Objects.equals(userId, ownerId))
                .orElseGet(() -> false);
        redisResourceAccess.setCachedOwnership(ResourceType.CHAPTER, chapterId, userId, isOwner);
        return isOwner;
    }

    public boolean isLessonOwner(Long lessonId) {
        Long userId = getCurrentUserId();
        Boolean cached = redisResourceAccess.getCachedOwnership(ResourceType.LESSON, lessonId, userId);
        if (cached != null) {
            return cached;
        }
        boolean isOwner = lessonRepository
                .findInstructorIdByChapterId(lessonId)
                .map(ownerId -> Objects.equals(userId, ownerId))
                .orElseGet(() -> false);
        redisResourceAccess.setCachedOwnership(ResourceType.LESSON, lessonId, userId, isOwner);
        return isOwner;
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

    public boolean isPostOwner(Long postId) {
        Long owner = postRepository.findUserIdByPostId(postId).orElseThrow(() -> new InvalidIdException(postId));
        return isOwner(owner);
    }

    public boolean isReviewOwner(Long reviewId) {
        Long owner =
                reviewRepository.findUserIdByReviewId(reviewId).orElseThrow(() -> new InvalidIdException(reviewId));
        return isOwner(owner);
    }

    public boolean isUserOwner(Long userId) {
        return isOwner(userId);
    }
}
