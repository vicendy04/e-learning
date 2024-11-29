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

    // Create operations
    Course toEntity(CourseCreateReq request);

    // Update operations
    Course toEntity(CourseUpdateReq request);

    CourseUpdateRes toUpdateResponse(Course entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Course entity, CourseUpdateReq request);

    // Get operations
    //     @Mapping(target = "enrollmentCount", expression = "java(entity.getEnrollments().size())")
    //    @Mapping(target = "contents", expression = "java(mapContents(entity.getContents()))")
    CourseGetRes toGetResponse(Course entity);

    CourseListRes toCourseListResponse(Course entity);

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
