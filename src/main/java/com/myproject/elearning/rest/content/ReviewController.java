package com.myproject.elearning.rest.content;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;
import static com.myproject.elearning.security.SecurityUtils.getCurrentUserId;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewAddRes;
import com.myproject.elearning.dto.response.review.ReviewCourseRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.dto.response.review.ReviewUserRes;
import com.myproject.elearning.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/courses/{courseId}/reviews")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<ReviewAddRes> addReview(@PathVariable Long courseId, @Valid @RequestBody ReviewCreateReq request) {
        Long userId = getCurrentUserId();
        var newReview = reviewService.addReview(userId, courseId, request);
        return successRes("Đánh giá đã được tạo thành công", newReview);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isReviewOwner(#reviewId))")
    public ApiRes<ReviewUpdateRes> editReview(
            @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateReq request) {
        var editedReview = reviewService.editReview(reviewId, request);
        return successRes("Đánh giá đã được cập nhật thành công", editedReview);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/courses/{courseId}/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isReviewOwner(#reviewId))")
    public ApiRes<Void> delReview(@PathVariable Long courseId, @PathVariable Long reviewId) {
        reviewService.delReview(courseId, reviewId);
        return successRes("Đánh giá đã được xóa thành công", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/courses/{courseId}/reviews")
    public ApiRes<PagedRes<ReviewCourseRes>> getReviewsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var reviews = reviewService.getReviewsByCourse(courseId, pageable);
        return successRes("Lấy danh sách đánh giá thành công", reviews);
    }

    //    should not be in use
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/reviews")
    public ApiRes<PagedRes<ReviewUserRes>> getReviewsByUser(
            @PathVariable Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var reviews = reviewService.getReviewsByUser(userId, pageable);
        return successRes("Lấy danh sách đánh giá thành công", reviews);
    }
}
