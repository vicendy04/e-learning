package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.enrollment.EnrollmentStatusUpdateRequest;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetResponse;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@RestController
public class EnrollmentController {
    EnrollmentService enrollmentService;

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        EnrollmentResponse enrollment = enrollmentService.enrollCourse(curUserId, courseId);
        ApiResponse<EnrollmentResponse> response = wrapSuccessResponse("Enrolled successfully", enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Void>> unrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        enrollmentService.unrollCourse(curUserId, courseId);
        ApiResponse<Void> response = wrapSuccessResponse("Unrolled successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/users/enrollments")
    public ResponseEntity<ApiResponse<PagedResponse<EnrollmentGetResponse>>> getUserEnrollments(
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
        Long curUserId = SecurityUtils.getCurrentUserLoginId().orElseThrow(AnonymousUserException::new);
        PagedResponse<EnrollmentGetResponse> enrollments = enrollmentService.getUserEnrollments(curUserId, pageable);
        ApiResponse<PagedResponse<EnrollmentGetResponse>> response =
                wrapSuccessResponse("User enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostAuthorize("hasAnyRole('ADMIN') or returnObject.body.data.user.email == #jwt.subject")
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<EnrollmentGetResponse>> getEnrollment(
            @PathVariable Long enrollmentId, @AuthenticationPrincipal Jwt jwt) {
        EnrollmentGetResponse enrollment = enrollmentService.getEnrollment(enrollmentId);
        ApiResponse<EnrollmentGetResponse> response =
                wrapSuccessResponse("Enrollment retrieved successfully", enrollment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<ApiResponse<PagedResponse<EnrollmentGetResponse>>> getCourseEnrollments(
            @PathVariable Long courseId, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        PagedResponse<EnrollmentGetResponse> enrollments = enrollmentService.getCourseEnrollments(courseId, pageable);
        ApiResponse<PagedResponse<EnrollmentGetResponse>> response =
                wrapSuccessResponse("Course enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ApiResponse<EnrollmentGetResponse>> changeEnrollmentStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody EnrollmentStatusUpdateRequest statusUpdateInput) {
        EnrollmentGetResponse updatedEnrollment =
                enrollmentService.changeEnrollmentStatus(enrollmentId, statusUpdateInput.getStatus());
        ApiResponse<EnrollmentGetResponse> response =
                wrapSuccessResponse("Enrollment status changed successfully", updatedEnrollment);
        return ResponseEntity.ok(response);
    }
}
