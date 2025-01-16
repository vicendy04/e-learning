package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.lesson.LessonCreateReq;
import com.myproject.elearning.dto.request.lesson.LessonUpdateReq;
import com.myproject.elearning.dto.response.lesson.LessonGetRes;
import com.myproject.elearning.dto.response.lesson.LessonListRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface LessonMapper {

    Lesson toEntity(LessonCreateReq request);

    @Mapping(target = "chapterId", source = "chapter.id")
    LessonGetRes toGetRes(Lesson entity);

    LessonListRes toListRes(Lesson entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Lesson entity, LessonUpdateReq request);
}
