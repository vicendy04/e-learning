package com.myproject.elearning.mapper.course;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.course.CourseUpdateRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CourseUpdateMapper extends EntityMapper<CourseUpdateRequest, Course> {}
