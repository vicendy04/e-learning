package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface CourseMapper {
    Course toEntity(CourseCreateReq request);

    Course toEntity(CourseUpdateReq request);

    CourseUpdateRes toUpdateRes(Course entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateReq request);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "imageUrl", source = "instructor.imageUrl")
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getInstructor().getFirstName() + \" \" + entity.getInstructor().getLastName())")
    CourseGetRes toGetRes(Course entity);

    CourseListRes toListRes(Course entity);
}
