package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.review.ReviewCreateRequest;
import com.myproject.elearning.dto.request.review.ReviewUpdateRequest;
import com.myproject.elearning.dto.response.review.ReviewCourseResponse;
import com.myproject.elearning.dto.response.review.ReviewResponse;
import com.myproject.elearning.dto.response.review.ReviewUpdateResponse;
import com.myproject.elearning.dto.response.review.ReviewUserResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@RestController
public class ReviewController {
    ReviewService reviewService;

    @PostMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long courseId, @Valid @RequestBody ReviewCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        ReviewResponse review = reviewService.createReview(userId, courseId, request);
        ApiResponse<ReviewResponse> response = wrapSuccessResponse("Đánh giá đã được tạo thành công", review);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewUpdateResponse>> updateReview(
            @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewUpdateResponse review = reviewService.updateReview(reviewId, request);
        ApiResponse<ReviewUpdateResponse> response =
                wrapSuccessResponse("Đánh giá đã được cập nhật thành công", review);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        ApiResponse<Void> response = wrapSuccessResponse("Đánh giá đã được xóa thành công", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewCourseResponse>>> getReviewsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<ReviewCourseResponse> reviews = reviewService.getReviewsByCourse(courseId, pageable);
        ApiResponse<PagedResponse<ReviewCourseResponse>> response =
                wrapSuccessResponse("Lấy danh sách đánh giá thành công", reviews);
        return ResponseEntity.ok(response);
    }

    //    should not be in use
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewUserResponse>>> getReviewsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<ReviewUserResponse> reviews = reviewService.getReviewsByUser(userId, pageable);
        ApiResponse<PagedResponse<ReviewUserResponse>> response =
                wrapSuccessResponse("Lấy danh sách đánh giá thành công", reviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long courseId) {
        Double avgRating = reviewService.getAverageRating(courseId);
        ApiResponse<Double> response = wrapSuccessResponse("Lấy điểm đánh giá trung bình thành công", avgRating);
        return ResponseEntity.ok(response);
    }
}
