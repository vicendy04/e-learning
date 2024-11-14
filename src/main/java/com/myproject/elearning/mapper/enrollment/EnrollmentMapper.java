package com.myproject.elearning.mapper.enrollment;

import com.myproject.elearning.domain.Enrollment;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapperConfig.class,
        uses = {EnrollmentUserMapper.class, EnrollmentCourseMapper.class})
public interface EnrollmentMapper extends EntityMapper<EnrollmentResponse, Enrollment> {
    @Mapping(target = "status", expression = "java(enrollment.getStatus().name())")
    @Override
    EnrollmentResponse toDto(Enrollment enrollment);
}
