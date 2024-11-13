package com.myproject.elearning.mapper.content;

import com.myproject.elearning.domain.Content;
import com.myproject.elearning.dto.response.content.ContentGetResponse;
import com.myproject.elearning.dto.response.content.ContentListResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ContentListMapper extends EntityMapper<ContentListResponse, Content> {

    @Mapping(target = "contentType", expression = "java(content.getContentType().name())")
    @Mapping(target = "status", expression = "java(content.getStatus().name())")
    @Override
    ContentListResponse toDto(Content content);
}
