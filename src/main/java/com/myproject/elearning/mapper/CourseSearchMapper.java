package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.search.CourseDocument;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CourseSearchMapper {
    Course toEntity(CourseDocument request);

    @Mapping(target = "instructorName", source = "instructor.username")
    CourseDocument toCourseDocument(Course entity);
}
