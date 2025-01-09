package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterGetRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface ChapterMapper {
    Chapter toEntity(ChapterCreateReq request);

    Chapter toEntity(ChapterUpdateReq request);

    @Mapping(target = "courseId", source = "course.id")
    ChapterGetRes toGetResponse(Chapter entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Chapter entity, ChapterUpdateReq request);
}
