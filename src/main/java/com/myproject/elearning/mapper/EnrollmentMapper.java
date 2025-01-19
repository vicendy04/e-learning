package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.dto.response.enrollment.EnrollmentEditRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes;
import com.myproject.elearning.dto.response.enrollment.EnrollmentGetRes.CourseInfo;
import com.myproject.elearning.dto.response.enrollment.EnrollmentRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface EnrollmentMapper {

    EnrollmentMapper ENROLLMENT_MAPPER = Mappers.getMapper(EnrollmentMapper.class);

    Course toEntity(CourseInfo request);

    CourseInfo toCourseInfo(Course entity);

    EnrollmentRes toRes(Enrollment entity);

    EnrollmentGetRes toGetRes(Enrollment entity);

    EnrollmentEditRes toEditRes(Enrollment entity);
}
