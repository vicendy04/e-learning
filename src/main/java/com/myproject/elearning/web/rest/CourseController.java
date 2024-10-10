package com.myproject.elearning.web.rest;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.service.CourseService;
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
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blank course.
     */
    @PostMapping("/courses")
    public ResponseEntity<Course> createBlankCourse(@Valid @RequestBody Course course) {
        Course blankCourse = courseService.createBlankCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(blankCourse);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable(name = "id") Long id) {
        Course course = courseService.getCourse(id);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    /**
     * @param course The course which contains the updated information. It should not have any modules.
     *               This API can not edit modules.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with the body.
     */
    @PutMapping("/courses")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(course);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
