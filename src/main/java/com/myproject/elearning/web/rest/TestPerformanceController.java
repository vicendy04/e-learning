package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.repository.CourseRepository;
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
        return successRes("Increment successful", "Success");
    }

    @Transactional
    @PostMapping("/enroll-count/decrement/{courseId}")
    public ApiRes<String> testDecrementEnrollCount(@PathVariable Long courseId) {
        courseRepository.decrementEnrollmentCount(courseId);
        return successRes("Decrement successful", "Success");
    }

    // Test concurrent enrollments
    @PostMapping("/enroll-concurrent/{courseId}")
    public ApiRes<EnrollmentRes> testConcurrentEnroll(@PathVariable Long courseId, @RequestParam Long userId) {
        EnrollmentRes enrollment = enrollService.enrollCourse(courseId, userId);
        return successRes("Enrollment successful", enrollment);
    }
}
