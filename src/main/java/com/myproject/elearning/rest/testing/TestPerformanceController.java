package com.myproject.elearning.rest.testing;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.rest.utils.ResponseUtils;
import com.myproject.elearning.service.EnrollService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/test-performance")
@RestController
public class TestPerformanceController {
    CourseRepository courseRepository;
    EnrollService enrollService;

    @Transactional
    @PostMapping("/enroll-count/increment/{courseId}")
    public ApiRes<String> testIncrementEnrollCount(@PathVariable Long courseId) {
        courseRepository.incrementEnrollmentCount(courseId);
        return ResponseUtils.successRes("Increment successful", "Success");
    }

    @Transactional
    @PostMapping("/enroll-count/decrement/{courseId}")
    public ApiRes<String> testDecrementEnrollCount(@PathVariable Long courseId) {
        courseRepository.decrementEnrollmentCount(courseId);
        return ResponseUtils.successRes("Decrement successful", "Success");
    }

    // Test concurrent enrollments
    @PostMapping("/enroll-concurrent/{courseId}")
    public ApiRes<EnrollmentRes> testConcurrentEnroll(@PathVariable Long courseId, @RequestParam Long userId) {
        EnrollmentRes enrollment = enrollService.enrollCourse(courseId, userId);
        return ResponseUtils.successRes("Enrollment successful", enrollment);
    }
}
