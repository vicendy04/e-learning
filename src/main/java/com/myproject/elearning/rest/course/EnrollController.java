package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.enrollment.EnrollStatusUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentEditRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.EnrollService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@RestController
public class EnrollController {
    EnrollService enrollService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<EnrollmentRes> enrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        EnrollmentRes enrollment = enrollService.enrollCourse(courseId, curUserId);
        return successRes("Enrolled successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/courses/{courseId}/unroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<Void> unrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        enrollService.unrollCourse(courseId, curUserId);
        return successRes("Unrolled successfully", null);
    }

    // Danger: @PostAuthorize should not be used as it only checks after method execution, which could lead to unwanted
    // data changes
    // @PostAuthorize("hasAnyRole('ADMIN') or returnObject.data.user.id == #jwt.subject")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentGetRes> getEnrollment(@PathVariable Long enrollmentId) {
        EnrollmentGetRes enrollment = enrollService.getEnrollment(enrollmentId);
        return successRes("Enrollment retrieved successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/enrollments/{enrollmentId}/status")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentEditRes> changeEnrollStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody EnrollStatusUpdateReq statusUpdateInput) {
        EnrollmentEditRes editedEnrollment = enrollService.changeEnrollStatus(enrollmentId, statusUpdateInput);
        return successRes("Enrollment status changed successfully", editedEnrollment);
    }
}
