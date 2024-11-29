package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.enrollment.EnrollStatusUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollService;
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
public class EnrollController {
    EnrollService enrollService;

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiRes<EnrollmentRes>> enrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        EnrollmentRes enrollment = enrollService.enrollCourse(curUserId, courseId);
        ApiRes<EnrollmentRes> response = successRes("Enrolled successfully", enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiRes<Void>> unrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        enrollService.unrollCourse(curUserId, courseId);
        ApiRes<Void> response = successRes("Unrolled successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/users/enrollments")
    public ResponseEntity<ApiRes<PagedRes<EnrollmentGetRes>>> getUserEnrollments(
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<EnrollmentGetRes> enrollments = enrollService.getUserEnrollments(curUserId, pageable);
        ApiRes<PagedRes<EnrollmentGetRes>> response =
                successRes("User enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostAuthorize("hasAnyRole('ADMIN') or returnObject.body.data.user.email == #jwt.subject")
    @GetMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiRes<EnrollmentGetRes>> getEnrollment(
            @PathVariable Long enrollmentId, @AuthenticationPrincipal Jwt jwt) {
        EnrollmentGetRes enrollment = enrollService.getEnrollment(enrollmentId);
        ApiRes<EnrollmentGetRes> response = successRes("Enrollment retrieved successfully", enrollment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<ApiRes<PagedRes<EnrollmentGetRes>>> getCourseEnrollments(
            @PathVariable Long courseId, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        PagedRes<EnrollmentGetRes> enrollments = enrollService.getCourseEnrollments(courseId, pageable);
        ApiRes<PagedRes<EnrollmentGetRes>> response =
                successRes("Course enrollments retrieved successfully", enrollments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ApiRes<EnrollmentGetRes>> changeEnrollStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody EnrollStatusUpdateReq statusUpdateInput) {
        EnrollmentGetRes updatedEnrollment =
                enrollService.changeEnrollStatus(enrollmentId, statusUpdateInput.getStatus());
        ApiRes<EnrollmentGetRes> response = successRes("Enrollment status changed successfully", updatedEnrollment);
        return ResponseEntity.ok(response);
    }
}
