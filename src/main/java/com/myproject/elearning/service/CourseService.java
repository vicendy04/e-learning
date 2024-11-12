package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.PagedResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing courses.
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

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
        currentCourse.setDescription(course.getDescription());
        return courseRepository.save(currentCourse);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        courseRepository.delete(course);
    }

    public PagedResponse<Course> getAllCourses(Pageable pageable) {
        Page<Course> courses = courseRepository.findAllWithContents(pageable);
        return PagedResponse.from(courses);
    }
}
