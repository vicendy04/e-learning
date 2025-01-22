package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseSearch;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseAddRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.EnrollmentRepository;
import com.myproject.elearning.repository.TopicRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.test.CourseFilters;
import com.myproject.elearning.service.test.CourseSearcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.myproject.elearning.mapper.CourseMapper.COURSE_MAPPER;

/**
 * Service class for managing courses.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseService {
    CourseRepository courseRepository;
    EnrollmentRepository enrollmentRepository;
    UserRepository userRepository;
    TopicRepository topicRepository;

    @Transactional
    public CourseAddRes addCourse(Long instructorId, CourseCreateReq courseCreateReq) {
        Course course = COURSE_MAPPER.toEntity(courseCreateReq);
        User user = userRepository.getReferenceById(instructorId);
        course.setInstructor(user);
        Topic topic = topicRepository.getReferenceById(courseCreateReq.getTopicId());
        course.setTopic(topic);
        Course savedCourse = courseRepository.save(course);
        return COURSE_MAPPER.toAddRes(savedCourse);
    }

    @Transactional(readOnly = true)
    public CourseData getDBCourse(Long courseId) {
        Course course =
                courseRepository.findWithInstructorAndTopicById(courseId).orElseThrow(() -> new InvalidIdEx(courseId));
        return COURSE_MAPPER.toData(course);
    }

    public int countEnrollments(Long id) {
        return enrollmentRepository.countEnrollmentByCourseId(id);
    }

    // note
    @Transactional
    public CourseUpdateRes editCourse(Long id, CourseUpdateReq request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new InvalidIdEx(id));
        COURSE_MAPPER.partialUpdate(course, request);
        Course savedCourse = courseRepository.save(course);
        return COURSE_MAPPER.toUpdateRes(savedCourse);
    }

    @Transactional
    public void delCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PagedRes<CourseListRes> getCourses(CourseSearch searchDTO, Pageable pageable) {
//        Specification<Course> spec = CourseSpec.filterCourses(searchDTO);
        Page<Course> all = courseRepository.findAllBy(pageable);
        Page<CourseListRes> map = all.map(COURSE_MAPPER::toListRes);
        return PagedRes.from(map);
    }

    @Transactional(readOnly = true)
    public PagedRes<CourseListRes> getCourses2(CourseFilters filters, CourseSearcher searcher) {
        Page<Course> courses = searcher.search(filters);
        return PagedRes.from(courses.map(COURSE_MAPPER::toListRes));
    }

    public PagedRes<CourseListRes> getCoursesByInstructorId(Long instructorId, Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAllByInstructorId(instructorId, pageable);
        return PagedRes.from(coursePage.map(COURSE_MAPPER::toListRes));
    }

    public PagedRes<CourseListRes> getEnrolledCourses(Long userId, Pageable pageable) {
        Page<Course> enrolledCourses = courseRepository.findAllByEnrollmentUserId(userId, pageable);
        return PagedRes.from(enrolledCourses.map(COURSE_MAPPER::toListRes));
    }
}
