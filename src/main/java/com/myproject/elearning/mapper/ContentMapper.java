package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.dto.response.content.ContentListResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.List;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface ContentMapper {

    // Create operations
    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
    //    @Mapping(target = "course", source = "courseId", qualifiedByName = "toCourse")
    Content toEntity(ContentCreateRequest request);

    // Update operations
    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
    Content toEntity(ContentUpdateRequest request);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Content entity, ContentUpdateRequest request);

    // Get operations
    @Mapping(target = "contentType", expression = "java(entity.getContentType().name())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    ContentGetResponse toGetResponse(Content entity);

    // List operations
    @Mapping(target = "contentType", expression = "java(entity.getContentType().name())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    ContentListResponse toListResponse(Content entity);

    List<ContentListResponse> toListResponse(List<Content> entities);

    //    @Named("toCourse")
    //    default Course toCourse(Long courseId) {
    //        if (courseId == null) {
    //            return null;
    //        }
    //        Course course = new Course();
    //        course.setId(courseId);
    //        return course;
    //    }
}
