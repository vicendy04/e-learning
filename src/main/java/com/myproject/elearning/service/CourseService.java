package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseSearchDTO;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.CourseMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.repository.specification.CourseSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    CourseMapper courseMapper;
    UserRepository userRepository;

    @Transactional
    public CourseGetRes addCourse(Long instructorId, CourseCreateReq courseCreateReq) {
        Course course = courseMapper.toEntity(courseCreateReq);
        User userRef = userRepository.getReferenceById(instructorId);
        course.setInstructor(userRef);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toGetResponse(savedCourse);
    }

    public CourseGetRes getCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        return courseMapper.toGetResponse(course);
    }

    public int countEnrollments(Long id) {
        return courseRepository.countEnrollmentsByCourseId(id);
    }

    @Transactional
    public CourseUpdateRes editCourse(Long id, CourseUpdateReq request) {
        Course course = courseRepository.getReferenceIfExists(id);
        courseMapper.partialUpdate(course, request);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toUpdateResponse(savedCourse);
    }

    public void delCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new InvalidIdException(id);
        }
        courseRepository.deleteByCourseId(id);
    }

    public PagedRes<CourseListRes> getCourses(CourseSearchDTO searchDTO, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.filterCourses(searchDTO);
        return PagedRes.from(courseRepository.findAll(spec, pageable).map(courseMapper::toCourseListResponse));
    }
}
