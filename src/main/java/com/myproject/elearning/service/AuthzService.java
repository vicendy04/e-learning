package com.myproject.elearning.service;

import static com.myproject.elearning.constant.AuthoritiesConstants.ADMIN;
import static com.myproject.elearning.security.SecurityUtils.hasCurrentUserNoneOfAuthorities;

import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.*;
import com.myproject.elearning.security.SecurityUtils;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthzService {
    EnrollmentRepository enrollmentRepository;
    CourseRepository courseRepository;
    PostRepository postRepository;
    ReviewRepository reviewRepository;
    DiscountRepository discountRepository;

    private void validateAccess(Long reqOwner, Long owner, String message) {
        if (!Objects.equals(reqOwner, owner)) {
            throw new AccessDeniedException(message);
        }
    }

    private Long getCurrentUserId() {
        return SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
    }

    public void checkEnrollmentAccess(Long enrollmentId) {
        if (hasCurrentUserNoneOfAuthorities(ADMIN)) {
            Long owner = enrollmentRepository
                    .findUserIdByEnrollmentId(enrollmentId)
                    .orElseThrow(() -> new InvalidIdException(enrollmentId));
            validateAccess(getCurrentUserId(), owner, "Bạn không có quyền xem chi tiết đăng ký này");
        }
    }

    public void checkDiscountAccess(Long disCountId) {
        if (hasCurrentUserNoneOfAuthorities(ADMIN)) {
            Long owner = discountRepository
                    .findInstructorIdByCourseId(disCountId)
                    .orElseThrow(() -> new InvalidIdException(disCountId));
            validateAccess(getCurrentUserId(), owner, "Bạn không có quyền xem chi tiết phiếu giảm giá này");
        }
    }

    public void checkCourseAccess(Long courseId) {
        if (hasCurrentUserNoneOfAuthorities(ADMIN)) {
            Long owner = courseRepository
                    .findInstructorIdByCourseId(courseId)
                    .orElseThrow(() -> new InvalidIdException(courseId));
            validateAccess(getCurrentUserId(), owner, "Bạn không có quyền truy cập khóa học này");
        }
    }

    public void checkUserAccess(Long userId) {
        if (!getCurrentUserId().equals(userId) && hasCurrentUserNoneOfAuthorities(ADMIN)) {
            throw new AccessDeniedException("Bạn không có quyền sửa thông tin người dùng này");
        }
    }

    public void checkPostAccess(Long postId) {
        if (hasCurrentUserNoneOfAuthorities(ADMIN)) {
            Long owner = postRepository.findUserIdByPostId(postId).orElseThrow(() -> new InvalidIdException(postId));
            validateAccess(getCurrentUserId(), owner, "You don't have permission to access this post");
        }
    }

    public void checkReviewAccess(Long reviewId) {
        if (hasCurrentUserNoneOfAuthorities(ADMIN)) {
            Long owner =
                    reviewRepository.findUserIdByReviewId(reviewId).orElseThrow(() -> new InvalidIdException(reviewId));
            validateAccess(getCurrentUserId(), owner, "You don't have permission to access this review");
        }
    }
}
