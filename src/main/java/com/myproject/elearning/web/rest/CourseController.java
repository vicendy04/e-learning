package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapSuccessResponse;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.ApiResponse;
import com.myproject.elearning.dto.response.CourseGetResponse;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for courses
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    /**
     * @param course the blank course to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the new blank course wrapped in {@link ApiResponse}.
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Course>> createBlankCourse(@Valid @RequestBody Course course) {
        Course blankCourse = courseService.createBlankCourse(course);
        ApiResponse<Course> response = wrapSuccessResponse("Course created successfully", blankCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseGetResponse>> getCourse(@PathVariable(name = "id") Long id) {
        Course course = courseService.getCourse(id);
        CourseGetResponse courseGetResponse = CourseGetResponse.toDTO(course);
        ApiResponse<CourseGetResponse> response =
                wrapSuccessResponse("Course retrieved successfully", courseGetResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<Course>>> getAllCourses(
            @PageableDefault(size = 5, page = 0, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<Course> courses = courseService.getAllCourses(pageable);
        ApiResponse<PagedResponse<Course>> response =
                wrapSuccessResponse("All courses retrieved successfully", courses);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * @param course The course which contains the updated information. It should not have any modules.
     *               This API can not edit modules.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with the body.
     */
    @PutMapping("")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(course);
        ApiResponse<Course> response = wrapSuccessResponse("Course updated successfully", updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable(name = "id") Long id) {
        courseService.deleteCourse(id);
        ApiResponse<Void> response = wrapSuccessResponse("Course deleted successfully", null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
