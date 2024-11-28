package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.dto.common.ApiResponse;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.course.CourseCreateRequest;
import com.myproject.elearning.dto.request.course.CourseSearchDTO;
import com.myproject.elearning.dto.request.course.CourseUpdateRequest;
import com.myproject.elearning.dto.response.course.CourseGetResponse;
import com.myproject.elearning.dto.response.course.CourseListResponse;
import com.myproject.elearning.dto.response.course.CourseUpdateResponse;
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

    @PostMapping("")
    public ResponseEntity<ApiResponse<CourseGetResponse>> createCourse(
            @Valid @RequestBody CourseCreateRequest courseCreateRequest) {
        CourseGetResponse courseResponse = courseService.createCourse(courseCreateRequest);
        ApiResponse<CourseGetResponse> response = wrapSuccessResponse("Course created successfully", courseResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseGetResponse>> getCourse(@PathVariable(name = "id") Long id) {
        CourseGetResponse courseGetResponse = redisCourseService.getCachedCourse(id);
        if (courseGetResponse == null) {
            courseGetResponse = courseService.getCourse(id);
            redisCourseService.setCachedCourse(id, courseGetResponse);
        }
        Integer enrollmentCount = redisCourseService.getCachedEnrollmentCount(id);
        if (enrollmentCount == null) {
            enrollmentCount = courseService.countEnrollments(id);
            redisCourseService.setCachedEnrollmentCount(id, enrollmentCount);
        }
        courseGetResponse.setEnrollmentCount(enrollmentCount);

        ApiResponse<CourseGetResponse> response =
                wrapSuccessResponse("Course retrieved successfully", courseGetResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<CourseListResponse>>> getCourses(
            @ModelAttribute CourseSearchDTO searchDTO,
            @PageableDefault(size = 10, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<CourseListResponse> courses = courseService.getCourses(searchDTO, pageable);
        ApiResponse<PagedResponse<CourseListResponse>> response =
                wrapSuccessResponse("Courses retrieved successfully", courses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseUpdateResponse>> updateCourse(
            @PathVariable(name = "id") Long id, @RequestBody CourseUpdateRequest courseUpdateRequest) {
        CourseUpdateResponse updatedCourse = courseService.updateCourse(id, courseUpdateRequest);
        //        courseCacheService.invalidateCache(id); // note
        ApiResponse<CourseUpdateResponse> response = wrapSuccessResponse("Course updated successfully", updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable(name = "id") Long id) {
        courseService.deleteCourse(id);
        //        courseCacheService.invalidateCache(id); // note
        ApiResponse<Void> response = wrapSuccessResponse("Course deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
