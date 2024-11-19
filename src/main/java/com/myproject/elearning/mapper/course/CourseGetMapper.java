package com.myproject.elearning.mapper.course;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.response.course.CourseGetResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CourseGetMapper extends EntityMapper<CourseGetResponse, Course> {
    @Mapping(target = "enrollmentCount", expression = "java(course.getEnrollments().size())")
    //    @Mapping(target = "contents", expression = "java(mapContents(course.getContents()))")
    @Override
    CourseGetResponse toDto(Course course);

    //    @Named("mapContents")
    //    default List<CourseGetResponse.ContentDTO> mapContents(List<Content> contents) {
    //        if (contents == null) {
    //            return Collections.emptyList();
    //        }
    //        List<CourseGetResponse.ContentDTO> contentDTOs = new ArrayList<>();
    //        for (Content content : contents) {
    //            contentDTOs.add(new CourseGetResponse.ContentDTO(
    //                    content.getId(),
    //                    content.getTitle(),
    //                    content.getOrderIndex(),
    //                    content.getStatus().name()));
    //        }
    //        return contentDTOs;
    //    }
}
