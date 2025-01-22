package com.myproject.elearning.service;

import static com.myproject.elearning.mapper.CourseMapper.COURSE_MAPPER;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.common.PagedRes;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseAddRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.dto.search.CourseFilters;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.TopicRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.strategy.CourseSearcher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public CourseData getCourse(Long courseId) {
        Course course =
                courseRepository.findWithInstructorAndTopicById(courseId).orElseThrow(() -> new InvalidIdEx(courseId));
        return COURSE_MAPPER.toData(course);
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
    public Page<CourseData> getCourses(CourseFilters filters, PageRequest pageRequest, CourseSearcher searcher) {
        return searcher.search(filters, pageRequest);
    }

    public PagedRes<CourseListRes> getCoursesByInstructorId(Long instructorId, Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAllByInstructorId(instructorId, pageable);
        return PagedRes.of(coursePage.map(COURSE_MAPPER::toListRes));
    }

    public PagedRes<CourseListRes> getEnrolledCourses(Long userId, Pageable pageable) {
        Page<Course> enrolledCourses = courseRepository.findAllByEnrollmentUserId(userId, pageable);
        return PagedRes.of(enrolledCourses.map(COURSE_MAPPER::toListRes));
    }
}
