package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.common.PagedResponse;
import com.myproject.elearning.dto.request.course.CourseCreateRequest;
import com.myproject.elearning.dto.request.course.CourseSearchDTO;
import com.myproject.elearning.dto.request.course.CourseUpdateRequest;
import com.myproject.elearning.dto.response.course.CourseGetResponse;
import com.myproject.elearning.dto.response.course.CourseUpdateResponse;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.mapper.course.CourseCreateMapper;
import com.myproject.elearning.mapper.course.CourseGetMapper;
import com.myproject.elearning.mapper.course.CourseUpdateMapper;
import com.myproject.elearning.mapper.course.CourseUpdateResponseMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.specification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing courses.
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseGetMapper courseGetMapper;
    private final CourseCreateMapper courseCreateMapper;
    private final CourseUpdateMapper courseUpdateMapper;
    private final CourseUpdateResponseMapper courseUpdateResponseMapper;

    public CourseGetResponse createBlankCourse(CourseCreateRequest courseCreateRequest) {
        Course course = courseCreateMapper.toEntity(courseCreateRequest);
        Course savedCourse = courseRepository.save(course);
        return courseGetMapper.toDto(savedCourse);
    }

    public CourseGetResponse getCourse(Long id) {
        Course course = courseRepository.findWithEnrollmentsById(id).orElseThrow(() -> new InvalidIdException(id));
        return courseGetMapper.toDto(course);
    }

    @Transactional
    public CourseUpdateResponse updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        courseUpdateMapper.partialUpdate(course, request);
        Course savedCourse = courseRepository.save(course);
        return courseUpdateResponseMapper.toDto(savedCourse);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdException(id));
        courseRepository.delete(course);
    }

    public PagedResponse<CourseGetResponse> getAllCourses(CourseSearchDTO searchDTO, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.filterCourses(searchDTO);
        Page<CourseGetResponse> courses =
                courseRepository.findAll(spec, pageable).map(courseGetMapper::toDto);
        return PagedResponse.from(courses);
    }
}
