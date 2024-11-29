package com.myproject.elearning.mapper;

import static com.myproject.elearning.dto.response.enrollment.EnrollmentGetResponse.UserInfo;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetResponse;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetResponse.CourseInfo;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface EnrollmentMapper {

    Course toEntity(CourseInfo request);

    CourseInfo toCourseInfo(Course request);

    User toEntity(UserInfo request);

    UserInfo toUserInfo(User request);

    EnrollmentResponse toEnrollmentResponse(Enrollment entity);

    EnrollmentGetResponse toEnrollmentGetResponse(Enrollment entity);
}
