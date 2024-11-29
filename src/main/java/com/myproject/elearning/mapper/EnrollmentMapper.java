package com.myproject.elearning.mapper;

import static com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes.UserInfo;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes.CourseInfo;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface EnrollmentMapper {

    Course toEntity(CourseInfo request);

    CourseInfo toCourseInfo(Course request);

    User toEntity(UserInfo request);

    UserInfo toUserInfo(User request);

    EnrollmentRes toEnrollmentResponse(Enrollment entity);

    EnrollmentGetRes toEnrollmentGetResponse(Enrollment entity);
}
