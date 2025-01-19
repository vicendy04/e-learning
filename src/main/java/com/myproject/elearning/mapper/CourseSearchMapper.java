package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface CourseSearchMapper {
    CourseSearchMapper COURSE_SEARCH_MAPPER = Mappers.getMapper(CourseSearchMapper.class);

    Course toEntity(CourseDocument request);

    CourseDocument toCourseDocument(CourseGetRes entity);
}
