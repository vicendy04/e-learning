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
    @ResponseStatus(HttpStatus.CREATED)
    public ApiRes<EnrollmentRes> enrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        EnrollmentRes enrollment = enrollService.enrollCourse(curUserId, courseId);
        return successRes("Enrolled successfully", enrollment);
    }

    @DeleteMapping("/courses/{courseId}/enroll")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> unrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        enrollService.unrollCourse(curUserId, courseId);
        return successRes("Unrolled successfully", null);
    }

    @GetMapping("/users/enrollments")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<EnrollmentGetRes>> getUserEnrollments(
            @PageableDefault(size = 5, page = 0) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<EnrollmentGetRes> enrollments = enrollService.getUserEnrollments(curUserId, pageable);
        return successRes("User enrollments retrieved successfully", enrollments);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostAuthorize("hasAnyRole('ADMIN') or returnObject.data.user.email == #jwt.subject")
    @GetMapping("/enrollments/{enrollmentId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<EnrollmentGetRes> getEnrollment(@PathVariable Long enrollmentId, @AuthenticationPrincipal Jwt jwt) {
        EnrollmentGetRes enrollment = enrollService.getEnrollment(enrollmentId);
        return successRes("Enrollment retrieved successfully", enrollment);
    }

    @GetMapping("/courses/{courseId}/enrollments")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<EnrollmentGetRes>> getCourseEnrollments(
            @PathVariable Long courseId, @PageableDefault(size = 5, page = 0) Pageable pageable) {
        PagedRes<EnrollmentGetRes> enrollments = enrollService.getCourseEnrollments(courseId, pageable);
        return successRes("Course enrollments retrieved successfully", enrollments);
    }

    @PutMapping("/enrollments/{enrollmentId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<EnrollmentGetRes> changeEnrollStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody EnrollStatusUpdateReq statusUpdateInput) {
        EnrollmentGetRes updatedEnrollment =
                enrollService.changeEnrollStatus(enrollmentId, statusUpdateInput.getStatus());
        return successRes("Enrollment status changed successfully", updatedEnrollment);
    }
}
