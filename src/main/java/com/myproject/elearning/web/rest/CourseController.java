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

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody Course course) {
        Course newCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
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

    @PutMapping("/courses")
    public ResponseEntity<Course> updateUser(@RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(course);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
