package com.myproject.elearning.mapper.content;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.content.ContentCreateRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ContentCreateMapper extends EntityMapper<ContentCreateRequest, Content> {

    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
//    @Mapping(target = "course", source = "courseId", qualifiedByName = "toCourse")
    Content toEntity(ContentCreateRequest request);

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