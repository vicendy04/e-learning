package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.course.CourseCreateRequest;
import com.myproject.elearning.dto.request.course.CourseUpdateRequest;
import com.myproject.elearning.dto.response.course.CourseGetResponse;
import com.myproject.elearning.dto.response.course.CourseUpdateResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface CourseMapper {

    // Create operations
    Course toEntity(CourseCreateRequest request);

    // Update operations
    Course toEntity(CourseUpdateRequest request);

    CourseUpdateResponse toUpdateResponse(Course entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateRequest request);

    // Get operations
    // @Mapping(target = "enrollmentCount", expression = "java(course.getEnrollments().size())")
    //    @Mapping(target = "contents", expression = "java(mapContents(course.getContents()))")
    CourseGetResponse toGetResponse(Course entity);

    // List operations

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
