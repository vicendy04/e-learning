package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Review;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.review.ReviewCreateRequest;
import com.myproject.elearning.dto.request.review.ReviewUpdateRequest;
import com.myproject.elearning.dto.response.review.ReviewCourseResponse;
import com.myproject.elearning.dto.response.review.ReviewResponse;
import com.myproject.elearning.dto.response.review.ReviewUpdateResponse;
import com.myproject.elearning.dto.response.review.ReviewUserResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.ReviewMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.ReviewRepository;
import com.myproject.elearning.repository.UserRepository;
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
public class ReviewService {
    ReviewRepository reviewRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponse createReview(Long userId, Long courseId, ReviewCreateRequest request) {
        if (reviewRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("Người dùng đã đánh giá khóa học này");
        }

        User userRef = userRepository.getReferenceIfExists(userId);
        Course courseRef = courseRepository.getReferenceIfExists(courseId);

        Review review = reviewMapper.toEntity(request);
        review.setUser(userRef);
        review.setCourse(courseRef);

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewUpdateResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new InvalidIdException(reviewId));
        reviewMapper.partialUpdate(review, request);
        return reviewMapper.toUpdateResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long reviewId) {
        int deletedCount = reviewRepository.deleteByReviewId(reviewId);
        if (deletedCount == 0) throw new InvalidIdException(reviewId);
    }

    public PagedResponse<ReviewCourseResponse> getReviewsByCourse(Long courseId, Pageable pageable) {
        Page<ReviewCourseResponse> reviews = reviewRepository.findAllByCourseId(courseId, pageable);
        return PagedResponse.from(reviews);
    }

    public PagedResponse<ReviewUserResponse> getReviewsByUser(Long userId, Pageable pageable) {
        Page<ReviewUserResponse> reviews = reviewRepository.findAllByUserId(userId, pageable);
        return PagedResponse.from(reviews);
    }

    public Double getAverageRating(Long courseId) {
        return reviewRepository.getAverageRatingByCourseId(courseId);
    }
}
