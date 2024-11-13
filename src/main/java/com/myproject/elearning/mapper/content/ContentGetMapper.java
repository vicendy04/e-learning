package com.myproject.elearning.mapper.content;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ContentGetMapper extends EntityMapper<ContentGetResponse, Content> {

    @Mapping(target = "contentType", expression = "java(content.getContentType().name())")
    @Mapping(target = "status", expression = "java(content.getStatus().name())")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Override
    ContentGetResponse toDto(Content content);
}
