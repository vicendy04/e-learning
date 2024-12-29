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

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiRes<CourseGetRes> addCourse(@Valid @RequestBody CourseCreateReq courseCreateReq) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        CourseGetRes courseResponse = courseService.addCourse(curUserId, courseCreateReq);
        return successRes("Course created successfully", courseResponse);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<CourseGetRes> getCourse(@PathVariable(name = "id") Long id) {
        CourseGetRes courseGetRes = redisCourseService.getCachedCourse(id);
        if (courseGetRes == null) {
            courseGetRes = courseService.getCourse(id);
            redisCourseService.setCachedCourse(id, courseGetRes);
        }
        return successRes("Course retrieved successfully", courseGetRes);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<CourseListRes>> getCourses(
            @ModelAttribute CourseSearchDTO searchDTO,
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<CourseListRes> courses = courseService.getCourses(searchDTO, pageable);
        return successRes("Courses retrieved successfully", courses);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<CourseUpdateRes> editCourse(
            @PathVariable(name = "id") Long id, @RequestBody CourseUpdateReq courseUpdateReq) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        //        courseCacheService.invalidateCache(id); // note
        CourseUpdateRes updatedCourse = courseService.editCourse(id, curUserId, courseUpdateReq);
        return successRes("Course updated successfully", updatedCourse);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiRes<Void> delCourse(@PathVariable(name = "id") Long id) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        courseService.delCourse(id, curUserId);
        //        courseCacheService.invalidateCache(id); // note
        return successRes("Course deleted successfully", null);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @GetMapping("/my-courses")
    @ResponseStatus(HttpStatus.OK)
    public ApiRes<PagedRes<CourseListRes>> getMyCourses(
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        PagedRes<CourseListRes> courses = courseService.getCoursesByInstructorId(curUserId, pageable);
        return successRes("Lấy danh sách khóa học thành công", courses);
    }
}
