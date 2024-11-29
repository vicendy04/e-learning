package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.content.ContentCreateReq;
import com.myproject.elearning.dto.request.content.ContentUpdateReq;
import com.myproject.elearning.dto.response.content.ContentGetRes;
import com.myproject.elearning.dto.response.content.ContentListRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.List;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface ContentMapper {

    // Create operations
    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
    //    @Mapping(target = "course", source = "courseId", qualifiedByName = "toCourse")
    Content toEntity(ContentCreateReq request);

    // Update operations
    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
    Content toEntity(ContentUpdateReq request);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Content entity, ContentUpdateReq request);

    // Get operations
    @Mapping(target = "contentType", expression = "java(entity.getContentType().name())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    ContentGetRes toGetResponse(Content entity);

    // List operations
    @Mapping(target = "contentType", expression = "java(entity.getContentType().name())")
    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    ContentListRes toListResponse(Content entity);

    List<ContentListRes> toListResponse(List<Content> entities);

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
