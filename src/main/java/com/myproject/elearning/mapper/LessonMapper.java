package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface LessonMapper {
    LessonMapper LESSON_MAPPER = Mappers.getMapper(LessonMapper.class);

    @Mapping(target = "chapterId", source = "chapter.id")
    LessonRes toRes(Lesson entity);

    Lesson toEntity(LessonCreateReq request);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Lesson entity, LessonUpdateReq request);
}
