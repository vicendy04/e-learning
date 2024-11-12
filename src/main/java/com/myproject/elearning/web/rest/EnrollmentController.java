package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.dto.request.EnrollmentStatusUpdateInput;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Enrollment>> enrollCourse(@PathVariable Long courseId) {
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(AnonymousUserException::new);
        Enrollment enrollment = enrollmentService.enrollCourse(email, courseId);
        ApiResponse<Enrollment> response = wrapSuccessResponse("Enrolled successfully", enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Void>> unrollCourse(@PathVariable Long courseId) {
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(AnonymousUserException::new);
        enrollmentService.unrollCourse(email, courseId);
        ApiResponse<Void> response = wrapSuccessResponse("Unrolled successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/users/enrollments")
    public ResponseEntity<ApiResponse<PagedResponse<Enrollment>>> getUserEnrollments(
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
        String email = SecurityUtils.getCurrentUserLogin().orElseThrow(AnonymousUserException::new);
        PagedResponse<Enrollment> enrollments = enrollmentService.getUserEnrollments(email, pageable);
        ApiResponse<PagedResponse<Enrollment>> response =
                wrapSuccessResponse("User enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    /**
     * Get enrollment by ID
     * Only enrolled user or admin can access
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') ")
    @PostAuthorize("hasAnyRole('ADMIN') or returnObject.body.data.user.email == #jwt.subject")
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<Enrollment>> getEnrollment(
            @PathVariable Long enrollmentId, @AuthenticationPrincipal Jwt jwt) {
        Enrollment enrollment = enrollmentService.getEnrollment(enrollmentId);
        ApiResponse<Enrollment> response = wrapSuccessResponse("Enrollment retrieved successfully", enrollment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<ApiResponse<PagedResponse<Enrollment>>> getCourseEnrollments(
            @PathVariable Long courseId, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        PagedResponse<Enrollment> enrollments = enrollmentService.getCourseEnrollments(courseId, pageable);
        ApiResponse<PagedResponse<Enrollment>> response =
                wrapSuccessResponse("Course enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    /**
     * Change enrollment status
     * Only course owner or enrolled user can change status
     */
    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ApiResponse<Enrollment>> changeEnrollmentStatus(
            @PathVariable Long enrollmentId, @RequestBody EnrollmentStatusUpdateInput statusUpdateInput) {
        Enrollment updatedEnrollment =
                enrollmentService.changeEnrollmentStatus(enrollmentId, statusUpdateInput.getStatus());
        ApiResponse<Enrollment> response =
                wrapSuccessResponse("Enrollment status changed successfully", updatedEnrollment);
        return ResponseEntity.ok(response);
    }
}
