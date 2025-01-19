package com.myproject.elearning.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.CourseAddRes;
import com.myproject.elearning.dto.response.course.CourseGetRes;
import com.myproject.elearning.dto.response.course.CourseListRes;
import com.myproject.elearning.dto.response.course.CourseUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import com.myproject.elearning.rest.course.CourseChapterController;
import com.myproject.elearning.rest.course.CourseController;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface CourseMapper {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);

    Course toEntity(CourseCreateReq request);

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

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "topicId", source = "topic.id")
    CourseAddRes toAddRes(Course entity);

    CourseGetRes toGetRes(CourseData data);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "imageUrl", source = "instructor.imageUrl")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getInstructor().getFirstName() + \" \" + entity.getInstructor().getLastName())")
    CourseData toData(Course entity);

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    CourseListRes toListRes(Course entity);

    @AfterMapping
    default void addLinks(@MappingTarget CourseListRes dto) {
        dto.add(linkTo(methodOn(CourseController.class).getCourse(dto.getId())).withSelfRel());
    }

    @AfterMapping
    default void addLinks(@MappingTarget CourseGetRes dto) {
        dto.add(
                linkTo(methodOn(CourseChapterController.class).getChaptersByCourseId(dto.getId()))
                        .withRel("chapters"),
                linkTo(methodOn(CourseChapterController.class).getExpandedChapters(dto.getId()))
                        .withRel("nested_chapters"));
    }
}
