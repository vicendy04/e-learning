package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Review;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewCourseRes;
import com.myproject.elearning.dto.response.review.ReviewRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.dto.response.review.ReviewUserRes;
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
    public ReviewRes addReview(Long userId, Long courseId, ReviewCreateReq request) {
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
    public ReviewUpdateRes editReview(Long reviewId, ReviewUpdateReq request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new InvalidIdException(reviewId));
        reviewMapper.partialUpdate(review, request);
        return reviewMapper.toUpdateResponse(reviewRepository.save(review));
    }

    public void delReview(Long reviewId) {
        int deletedCount = reviewRepository.deleteByReviewId(reviewId);
        if (deletedCount == 0) throw new InvalidIdException(reviewId);
    }

    public PagedRes<ReviewCourseRes> getReviewsByCourse(Long courseId, Pageable pageable) {
        Page<ReviewCourseRes> reviews = reviewRepository.findAllByCourseId(courseId, pageable);
        return PagedRes.from(reviews);
    }

    public PagedRes<ReviewUserRes> getReviewsByUser(Long userId, Pageable pageable) {
        Page<ReviewUserRes> reviews = reviewRepository.findAllByUserId(userId, pageable);
        return PagedRes.from(reviews);
    }

    public Double getAverageRating(Long courseId) {
        return reviewRepository.getAverageRatingByCourseId(courseId);
    }
}
