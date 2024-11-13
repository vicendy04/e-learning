package com.myproject.elearning.mapper.enrollment;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse.CourseInfo;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface EnrollmentCourseMapper extends EntityMapper<CourseInfo, Course> {
}