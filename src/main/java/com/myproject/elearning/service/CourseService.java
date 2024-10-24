package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.service.dto.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing courses.
 */
@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createBlankCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
    }

    public Course updateCourse(Course course) {
        Course currentCourse =
                courseRepository.findById(course.getId()).orElseThrow(() -> new InvalidIdException(course.getId()));
        currentCourse.setTitle(course.getTitle());
        currentCourse.setOverview(course.getOverview());
        return courseRepository.save(currentCourse);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        courseRepository.delete(course);
    }

    public PagedResponse<Course> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAll(pageable);
        return PagedResponse.from(courses);
    }
}
