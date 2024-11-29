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
import com.myproject.elearning.service.cache.RedisCourseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping("")
    public ResponseEntity<ApiRes<CourseGetRes>> addCourse(@Valid @RequestBody CourseCreateReq courseCreateReq) {
        Long curUserId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserException::new);
        CourseGetRes courseResponse = courseService.addCourse(curUserId, courseCreateReq);
        ApiRes<CourseGetRes> response = successRes("Course created successfully", courseResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<CourseGetRes>> getCourse(@PathVariable(name = "id") Long id) {
        CourseGetRes courseGetRes = redisCourseService.getCachedCourse(id);
        if (courseGetRes == null) {
            courseGetRes = courseService.getCourse(id);
            redisCourseService.setCachedCourse(id, courseGetRes);
        }
        Integer enrollmentCount = redisCourseService.getCachedEnrollmentCount(id);
        if (enrollmentCount == null) {
            enrollmentCount = courseService.countEnrollments(id);
            redisCourseService.setCachedEnrollmentCount(id, enrollmentCount);
        }
        courseGetRes.setEnrollmentCount(enrollmentCount);

        ApiRes<CourseGetRes> response = successRes("Course retrieved successfully", courseGetRes);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiRes<PagedRes<CourseListRes>>> getCourses(
            @ModelAttribute CourseSearchDTO searchDTO,
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedRes<CourseListRes> courses = courseService.getCourses(searchDTO, pageable);
        ApiRes<PagedRes<CourseListRes>> response = successRes("Courses retrieved successfully", courses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<CourseUpdateRes>> editCourse(
            @PathVariable(name = "id") Long id, @RequestBody CourseUpdateReq courseUpdateReq) {
        CourseUpdateRes updatedCourse = courseService.editCourse(id, courseUpdateReq);
        //        courseCacheService.invalidateCache(id); // note
        ApiRes<CourseUpdateRes> response = successRes("Course updated successfully", updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delCourse(@PathVariable(name = "id") Long id) {
        courseService.delCourse(id);
        //        courseCacheService.invalidateCache(id); // note
        ApiRes<Void> response = successRes("Course deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
