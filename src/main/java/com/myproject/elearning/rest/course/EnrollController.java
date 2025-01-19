package com.myproject.elearning.rest.course;

import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.request.enrollment.EnrollStatusUpdateReq;
import com.myproject.elearning.dto.response.enrollment.EnrollmentEditRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
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
@RequestMapping("/api/v1/enrollments/{enrollmentId}")
@RestController
public class EnrollController {
    EnrollService enrollService;

    // Danger: @PostAuthorize should not be used as it only checks after method execution, which could lead to unwanted
    // data changes
    // @PostAuthorize("hasAnyRole('ADMIN') or returnObject.data.user.id == #jwt.subject")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentGetRes> getEnrollment(@PathVariable Long enrollmentId) {
        EnrollmentGetRes enrollment = enrollService.getEnrollment(enrollmentId);
        return successRes("Enrollment retrieved successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/status")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isEnrollmentOwner(#enrollmentId))")
    public ApiRes<EnrollmentEditRes> changeEnrollStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody EnrollStatusUpdateReq statusUpdateInput) {
        EnrollmentEditRes editedEnrollment = enrollService.changeEnrollStatus(enrollmentId, statusUpdateInput);
        return successRes("Enrollment status changed successfully", editedEnrollment);
    }
}
