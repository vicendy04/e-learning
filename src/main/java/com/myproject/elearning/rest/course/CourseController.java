package com.myproject.elearning.rest.course;

import static com.myproject.elearning.mapper.CourseMapper.COURSE_MAPPER;
import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseSearch;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserEx;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.CourseSearchService;
import com.myproject.elearning.service.CourseService;
import com.myproject.elearning.service.EnrollService;
import com.myproject.elearning.service.ReviewService;
import com.myproject.elearning.service.database.CourseDBService;
import com.myproject.elearning.service.redis.RedisCourseService;
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

/**
 * REST controller for courses
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/courses")
@RestController
public class CourseController {
    CourseDBService courseDBService;
    EnrollService enrollService;
    CourseService courseService;
    ReviewService reviewService;
    RedisCourseService redisCourseService;
    CourseSearchService courseSearchService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<CourseGetRes> addCourse(@Valid @RequestBody CourseCreateReq courseCreateReq) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        CourseData data = courseService.addCourse(curUserId, courseCreateReq);
        var res = COURSE_MAPPER.toGetRes(data);
        courseSearchService.indexCourse(res);
        return successRes("Course created successfully", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}")
    public ApiRes<CourseGetRes> getCourse(@PathVariable(name = "courseId") Long courseId) {
        CourseData data = courseService.getCourse(courseId);
        var res = COURSE_MAPPER.toGetRes(data);
        return successRes("Course retrieved successfully", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}/rating")
    public ApiRes<Double> getAverageRating(@PathVariable Long courseId) {
        Double avgRating = reviewService.getAverageRating(courseId);
        return successRes("Lấy điểm đánh giá trung bình thành công", avgRating);
    }

    // note
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<PagedRes<CourseListRes>> getCourses(
            @ModelAttribute CourseSearch searchDTO,
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<CourseListRes> courses = courseDBService.getCourses(searchDTO, pageable);
        return successRes("Courses retrieved successfully", courses);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{courseId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#id))")
    public ApiRes<CourseUpdateRes> editCourse(
            @PathVariable(name = "courseId") Long courseId, @RequestBody CourseUpdateReq courseUpdateReq) {
        CourseUpdateRes updatedCourse = courseDBService.editCourse(courseId, courseUpdateReq);
        redisCourseService.invalidateCourseCache(courseId);
        return successRes("Course updated successfully", updatedCourse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#id))")
    public ApiRes<Void> delCourse(@PathVariable(name = "courseId") Long courseId) {
        courseDBService.delCourse(courseId);
        redisCourseService.invalidateCourseCache(courseId);
        return successRes("Course deleted successfully", null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<EnrollmentRes> enrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        EnrollmentRes enrollment = enrollService.enrollCourse(courseId, curUserId);
        return successRes("Enrolled successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}/unroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<Void> unrollCourse(@PathVariable Long courseId) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        enrollService.unrollCourse(courseId, curUserId);
        return successRes("Unrolled successfully", null);
    }

    // note
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my-courses")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<PagedRes<CourseListRes>> getMyCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        PagedRes<CourseListRes> courses = courseDBService.getCoursesByInstructorId(curUserId, pageable);
        return successRes("Lấy danh sách khóa học thành công", courses);
    }

    // note
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrolled")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PagedRes<CourseListRes>> getEnrolledCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
        PagedRes<CourseListRes> enrolledCourses = courseDBService.getEnrolledCourses(curUserId, pageable);
        return successRes("Lấy danh sách khóa học đã đăng ký thành công", enrolledCourses);
    }
}
