package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewCourseRes;
import com.myproject.elearning.dto.response.review.ReviewRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.dto.response.review.ReviewUserRes;
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
    public ResponseEntity<ApiRes<ReviewRes>> addReview(
            @PathVariable Long courseId, @Valid @RequestBody ReviewCreateReq request) {
        Long userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        ReviewRes review = reviewService.addReview(userId, courseId, request);
        ApiRes<ReviewRes> response = successRes("Đánh giá đã được tạo thành công", review);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiRes<ReviewUpdateRes>> editReview(
            @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateReq request) {
        ReviewUpdateRes review = reviewService.editReview(reviewId, request);
        ApiRes<ReviewUpdateRes> response = successRes("Đánh giá đã được cập nhật thành công", review);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiRes<Void>> delReview(@PathVariable Long reviewId) {
        reviewService.delReview(reviewId);
        ApiRes<Void> response = successRes("Đánh giá đã được xóa thành công", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<ApiRes<PagedRes<ReviewCourseRes>>> getReviewsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedRes<ReviewCourseRes> reviews = reviewService.getReviewsByCourse(courseId, pageable);
        ApiRes<PagedRes<ReviewCourseRes>> response = successRes("Lấy danh sách đánh giá thành công", reviews);
        return ResponseEntity.ok(response);
    }

    //    should not be in use
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiRes<PagedRes<ReviewUserRes>>> getReviewsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedRes<ReviewUserRes> reviews = reviewService.getReviewsByUser(userId, pageable);
        ApiRes<PagedRes<ReviewUserRes>> response = successRes("Lấy danh sách đánh giá thành công", reviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/rating")
    public ResponseEntity<ApiRes<Double>> getAverageRating(@PathVariable Long courseId) {
        Double avgRating = reviewService.getAverageRating(courseId);
        ApiRes<Double> response = successRes("Lấy điểm đánh giá trung bình thành công", avgRating);
        return ResponseEntity.ok(response);
    }
}
