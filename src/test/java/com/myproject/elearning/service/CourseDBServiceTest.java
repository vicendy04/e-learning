package com.myproject.elearning.service;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.mapper.CourseMapper;
import com.myproject.elearning.repository.CourseRepository;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.database.CourseDBService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseDBServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseDBService courseDBService;

    private Course course;
    private User instructor;
    private CourseCreateReq createReq;
    private CourseGetRes getRes;
}
