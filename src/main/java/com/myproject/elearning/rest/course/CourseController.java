package com.myproject.elearning.rest.course;

import static com.myproject.elearning.mapper.CourseMapper.COURSE_MAPPER;
import static com.myproject.elearning.rest.utils.ResponseUtils.successRes;
import static com.myproject.elearning.security.SecurityUtils.getCurrentUserId;

import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.*;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.dto.response.lesson.LessonContentRes;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.rest.utils.PageBuilder;
import com.myproject.elearning.service.CourseService;
import com.myproject.elearning.service.EnrollService;
import com.myproject.elearning.service.LessonService;
import com.myproject.elearning.service.ReviewService;
import com.myproject.elearning.service.cache.CourseRedisService;
import com.myproject.elearning.service.strategy.CourseSearcher;
import com.myproject.elearning.service.strategy.StrategyFactory;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
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
    LessonService lessonService;
    CourseService courseService;
    EnrollService enrollService;
    ReviewService reviewService;
    StrategyFactory strategyFactory;
    CourseRedisService courseRedisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<CourseAddRes> addCourse(@Valid @RequestBody CourseCreateReq courseCreateReq) {
        Long userId = getCurrentUserId();
        var newCourse = courseService.addCourse(userId, courseCreateReq);
        return successRes("Course created successfully", newCourse);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}")
    public ApiRes<CourseGetRes> getCourse(@PathVariable(name = "courseId") Long courseId) {
        CourseData data = courseRedisService.getAside(courseId);
        var res = COURSE_MAPPER.toGetRes(data);
        return successRes("Course retrieved successfully", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}/rating")
    public ApiRes<Double> getAverageRating(@PathVariable Long courseId) {
        Double avgRating = reviewService.getAverageRating(courseId);
        return successRes("Lấy điểm đánh giá trung bình thành công", avgRating);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<PagedRes<TopicCoursesRes>> getCourses(@Valid @ModelAttribute CourseFilters filters) {
        CourseSearcher searcher = strategyFactory.getStrategy();
        var pageRequest = PageBuilder.of(filters, Sort.by("id"));
        Page<CourseData> courses = courseService.getCourses(filters, pageRequest, searcher);
        Page<TopicCoursesRes> topicCoursesRes = COURSE_MAPPER.toTopicCoursesRes(courses);
        var res = PagedRes.of(topicCoursesRes);
        return successRes("Courses retrieved successfully", res);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{courseId}/{lessonId}")
    public ApiRes<LessonContentRes> getLessonContent(
            @PathVariable(name = "courseId") Long courseId, @PathVariable Long lessonId) {
        var lesson = lessonService.getLessonContent(lessonId);
        return successRes("Content retrieved", lesson);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{courseId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#courseId))")
    public ApiRes<CourseUpdateRes> editCourse(
            @PathVariable(name = "courseId") Long courseId, @RequestBody CourseUpdateReq courseUpdateReq) {
        var editedCourse = courseService.editCourse(courseId, courseUpdateReq);
        courseRedisService.invalidateCourseCache(courseId);
        return successRes("Course updated successfully", editedCourse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#courseId))")
    public ApiRes<Void> delCourse(@PathVariable(name = "courseId") Long courseId) {
        courseService.delCourse(courseId);
        courseRedisService.invalidateCourseCache(courseId);
        return successRes("Course deleted successfully", null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<EnrollmentRes> enrollCourse(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();
        var enrollment = enrollService.enrollCourse(courseId, userId);
        return successRes("Enrolled successfully", enrollment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{courseId}/unroll")
    @PreAuthorize("isAuthenticated() and hasAnyRole('USER')")
    public ApiRes<Void> unrollCourse(@PathVariable Long courseId) {
        Long userId = getCurrentUserId();
        enrollService.unrollCourse(courseId, userId);
        return successRes("Unrolled successfully", null);
    }

    // note
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my-courses")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<PagedRes<CourseListRes>> getMyCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long userId = getCurrentUserId();
        var courses = courseService.getCoursesByInstructorId(userId, pageable);
        return successRes("Lấy danh sách khóa học thành công", courses);
    }

    // note
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrolled")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PagedRes<CourseListRes>> getEnrolledCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long userId = getCurrentUserId();
        var enrolledCourses = courseService.getEnrolledCourses(userId, pageable);
        return successRes("Lấy danh sách khóa học đã đăng ký thành công", enrolledCourses);
    }
}
