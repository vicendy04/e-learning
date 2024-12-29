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
import com.myproject.elearning.security.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
        // Todo: refactor duplicate logic (courseId:userId)
        Long currentUserId =
                SecurityUtils.getLoginId().orElseThrow(() -> new AccessDeniedException("User not authenticated"));
        if (!currentUserId.equals(instructorId)) {
            throw new AccessDeniedException("You can only create courses for yourself");
        }

        Course course = courseMapper.toEntity(courseCreateReq);
        User instructor = userRepository.getReferenceById(instructorId);
        course.setInstructor(instructor);
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

    // note
    @Transactional
    public CourseUpdateRes editCourse(Long id, Long userId, CourseUpdateReq request) {
        // Todo: refactor duplicate logic (courseId:userId)
        Course course = courseRepository.findWithInstructorById(id).orElseThrow(() -> new InvalidIdException(id));
        if (!userId.equals(course.getInstructor().getId())) {
            throw new AccessDeniedException("You don't have permission to edit this course");
        }
        courseMapper.partialUpdate(course, request);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toUpdateResponse(savedCourse);
    }

    @Transactional
    public void delCourse(Long id, Long userId) {
        // Todo: refactor duplicate logic (courseId:userId)
        Course course = courseRepository.findWithInstructorById(id).orElseThrow(() -> new InvalidIdException(id));
        if (!userId.equals(course.getInstructor().getId())) {
            throw new AccessDeniedException("You don't have permission to delete this course");
        }
        courseRepository.deleteByCourseId(id);
    }

    public PagedRes<CourseListRes> getCourses(CourseSearchDTO searchDTO, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.filterCourses(searchDTO);
        return PagedRes.from(courseRepository.findAll(spec, pageable).map(courseMapper::toCourseListResponse));
    }

    public PagedRes<CourseListRes> getCoursesByInstructorId(Long instructorId, Pageable pageable) {
        Page<Course> coursePage = courseRepository.findByInstructorId(instructorId, pageable);
        return PagedRes.from(coursePage.map(courseMapper::toCourseListResponse));
    }
}
