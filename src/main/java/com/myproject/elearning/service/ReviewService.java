package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.ReviewMapper.REVIEW_MAPPER;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Review;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewAddRes;
import com.myproject.elearning.dto.response.review.ReviewCourseRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.dto.response.review.ReviewUserRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
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

    @Transactional
    public ReviewAddRes addReview(Long userId, Long courseId, ReviewCreateReq request) {
        if (reviewRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("Người dùng đã đánh giá khóa học này");
        }
        Review review = REVIEW_MAPPER.toEntity(request);
        User userRef = userRepository.getReferenceById(userId);
        Course courseRef = courseRepository.getReferenceById(courseId);
        review.setUser(userRef);
        review.setCourse(courseRef);
        courseRepository.incrementReviewCount(courseId);
        return REVIEW_MAPPER.toAddRes(reviewRepository.save(review));
    }

    @Transactional
    public ReviewUpdateRes editReview(Long reviewId, ReviewUpdateReq request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new InvalidIdEx(reviewId));
        REVIEW_MAPPER.partialUpdate(review, request);
        return REVIEW_MAPPER.toUpdateRes(reviewRepository.save(review));
    }

    @Transactional
    public void delReview(Long courseId, Long reviewId) {
        reviewRepository.deleteById(reviewId);
        courseRepository.decrementReviewCount(courseId);
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
        return courseRepository.getAverageRatingById(courseId);
    }
}
