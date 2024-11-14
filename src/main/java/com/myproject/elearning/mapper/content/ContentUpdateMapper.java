package com.myproject.elearning.mapper.content;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.request.content.ContentUpdateRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ContentUpdateMapper extends EntityMapper<ContentUpdateRequest, Content> {
    @Mapping(target = "contentType", expression = "java(Content.ContentType.valueOf(request.getContentType()))")
    @Mapping(target = "status", expression = "java(Content.ContentStatus.valueOf(request.getStatus()))")
    Content toEntity(ContentUpdateRequest request);
}
