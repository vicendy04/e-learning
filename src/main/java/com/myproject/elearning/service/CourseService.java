package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseSearch;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.mapper.CourseMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.repository.specification.CourseSpec;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing courses.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseService {
    CourseRepository courseRepository;
    UserRepository userRepository;
    CourseMapper courseMapper;

    @Transactional
    public CourseGetRes addCourse(Long instructorId, CourseCreateReq courseCreateReq) {
        Course course = courseMapper.toEntity(courseCreateReq);
        User user = userRepository.findById(instructorId).orElseThrow(() -> new InvalidIdEx(instructorId));
        course.setInstructor(user);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toGetRes(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseGetRes getCourse(Long id) {
        Course course = courseRepository.findWithInstructorById(id).orElseThrow(() -> new InvalidIdEx(id));
        return courseMapper.toGetRes(course);
    }

    public int countEnrollments(Long id) {
        return courseRepository.countEnrollmentsByCourseId(id);
    }

    // note
    @Transactional
    public CourseUpdateRes editCourse(Long id, CourseUpdateReq request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdEx(id));
        courseMapper.partialUpdate(course, request);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toUpdateRes(savedCourse);
    }

    @Transactional
    public void delCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public PagedRes<CourseListRes> getCourses(CourseSearch searchDTO, Pageable pageable) {
        Specification<Course> spec = CourseSpec.filterCourses(searchDTO);
        return PagedRes.from(courseRepository.findAll(spec, pageable).map(courseMapper::toListRes));
    }

    public PagedRes<CourseListRes> getCoursesByInstructorId(Long instructorId, Pageable pageable) {
        Page<Course> coursePage = courseRepository.findByInstructorId(instructorId, pageable);
        return PagedRes.from(coursePage.map(courseMapper::toListRes));
    }

    public PagedRes<CourseListRes> getEnrolledCourses(Long userId, Pageable pageable) {
        Page<Course> enrolledCourses = courseRepository.findByEnrollmentsUserId(userId, pageable);
        return PagedRes.from(enrolledCourses.map(courseMapper::toListRes));
    }
}
