package com.myproject.elearning.mapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.CourseData;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.course.CourseUpdateReq;
import com.myproject.elearning.dto.response.course.*;
import com.myproject.elearning.mapper.base.MapperConfig;
import com.myproject.elearning.rest.course.CourseChapterController;
import com.myproject.elearning.rest.course.CourseController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(config = MapperConfig.class)
public interface CourseMapper {
    CourseMapper COURSE_MAPPER = Mappers.getMapper(CourseMapper.class);

    TopicCoursesRes.CourseInfo toCourseInfo(CourseData course);

    default Page<TopicCoursesRes> toTopicCoursesRes(Page<CourseData> page) {
        Map<Long, TopicCoursesRes> groupMap = new HashMap<>();

        for (CourseData course : page) {
            TopicCoursesRes group = groupMap.computeIfAbsent(course.getTopicId(), id -> {
                TopicCoursesRes newGroup = new TopicCoursesRes();
                newGroup.setTopicId(id);
                newGroup.setTopicName(course.getTopicName());
                newGroup.setCourses(new ArrayList<>());
                return newGroup;
            });

            group.getCourses().add(toCourseInfo(course));
        }

        return new PageImpl<>(new ArrayList<>(groupMap.values()), page.getPageable(), page.getTotalElements());
    }

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "imageUrl", source = "instructor.imageUrl")
    CourseGetRes toGetRes(Course entity);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "topicId", source = "topic.id")
    CourseAddRes toAddRes(Course entity);

    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "imageUrl", source = "instructor.imageUrl")
    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    CourseData toData(Course entity);

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicName", source = "topic.name")
    CourseListRes toListRes(Course entity);

    Course toEntity(CourseCreateReq request);

    CourseUpdateRes toUpdateRes(Course entity);

    CourseGetRes toGetRes(CourseData data);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateReq request);

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
