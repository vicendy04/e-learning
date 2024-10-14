package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.service.CourseService;
import com.myproject.elearning.service.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for courses
 */
@RestController
@RequestMapping("/api/v1/")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * @param course the blank course to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the new blank course wrapped in {@link ApiResponse}.
     */
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> createBlankCourse(@Valid @RequestBody Course course) {
        Course blankCourse = courseService.createBlankCourse(course);
        ApiResponse<Course> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Course created successfully");
        response.setData(blankCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable(name = "id") Long id) {
        Course course = courseService.getCourse(id);
        ApiResponse<Course> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Course retrieved successfully");
        response.setData(course);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        ApiResponse<List<Course>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("All courses retrieved successfully");
        response.setData(courses);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * @param course The course which contains the updated information. It should not have any modules.
     *               This API can not edit modules.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with the body.
     */
    @PutMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(course);
        ApiResponse<Course> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Course updated successfully");
        response.setData(updatedCourse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable(name = "id") Long id) {
        courseService.deleteCourse(id);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Course deleted successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
