package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.successRes;

import com.myproject.elearning.dto.common.ApiRes;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseSearchDTO;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.exception.problemdetails.AnonymousUserException;
import com.myproject.elearning.security.SecurityUtils;
import com.myproject.elearning.service.CourseSearchService;
import com.myproject.elearning.service.CourseService;
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
    CourseService courseService;
    RedisCourseService redisCourseService;
    CourseSearchService courseSearchService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<CourseGetRes> addCourse(@Valid @RequestBody CourseCreateReq courseCreateReq) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        CourseGetRes courseResponse = courseService.addCourse(curUserId, courseCreateReq);
        courseSearchService.indexCourse(courseResponse);
        return successRes("Course created successfully", courseResponse);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ApiRes<CourseGetRes> getCourse(@PathVariable(name = "id") Long id) {
        CourseGetRes courseGetRes = redisCourseService.getCachedCourse(id);
        if (courseGetRes == null) {
            courseGetRes = courseService.getCourse(id);
            redisCourseService.setCachedCourse(id, courseGetRes);
        }
        return successRes("Course retrieved successfully", courseGetRes);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public ApiRes<PagedRes<CourseListRes>> getCourses(
            @ModelAttribute CourseSearchDTO searchDTO,
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<CourseListRes> courses = courseService.getCourses(searchDTO, pageable);
        return successRes("Courses retrieved successfully", courses);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#id))")
    public ApiRes<CourseUpdateRes> editCourse(
            @PathVariable(name = "id") Long id, @RequestBody CourseUpdateReq courseUpdateReq) {
        CourseUpdateRes updatedCourse = courseService.editCourse(id, courseUpdateReq);
        return successRes("Course updated successfully", updatedCourse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or @resourceAccessService.isCourseOwner(#id))")
    public ApiRes<Void> delCourse(@PathVariable(name = "id") Long id) {
        courseService.delCourse(id);
        //        courseCacheService.invalidateCache(id); // note
        return successRes("Course deleted successfully", null);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my-courses")
    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiRes<PagedRes<CourseListRes>> getMyCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<CourseListRes> courses = courseService.getCoursesByInstructorId(curUserId, pageable);
        return successRes("Lấy danh sách khóa học thành công", courses);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/enrolled")
    @PreAuthorize("isAuthenticated()")
    public ApiRes<PagedRes<CourseListRes>> getEnrolledCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<CourseListRes> enrolledCourses = courseService.getEnrolledCourses(curUserId, pageable);
        return successRes("Lấy danh sách khóa học đã đăng ký thành công", enrolledCourses);
    }
}
