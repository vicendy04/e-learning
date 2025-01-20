package com.myproject.elearning.mapper;

import static com.myproject.elearning.dto.response.chapter.ExpandedChapterRes.Item;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.myproject.elearning.domain.Chapter;
import com.myproject.elearning.domain.Lesson;
import com.myproject.elearning.dto.request.chapter.ChapterCreateReq;
import com.myproject.elearning.dto.request.chapter.ChapterUpdateReq;
import com.myproject.elearning.dto.response.chapter.ChapterRes;
import com.myproject.elearning.dto.response.chapter.ExpandedChapterRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import com.myproject.elearning.rest.course.ChapterController;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface ChapterMapper {
    ChapterMapper CHAPTER_MAPPER = Mappers.getMapper(ChapterMapper.class);

    @Mapping(source = "lessons", target = "items")
    ExpandedChapterRes toExpandedChapterRes(Chapter entity);

    Chapter toEntity(ChapterCreateReq request);

    Chapter toEntity(ChapterUpdateReq request);

    ChapterRes toRes(Chapter entity);

    Item toItem(Lesson entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Chapter entity, ChapterUpdateReq request);

    @AfterMapping
    default void addLinks(@MappingTarget ChapterRes dto) {
        dto.add(linkTo(methodOn(ChapterController.class).getLessonsByChapterId(dto.getId()))
                .withRel("lessons"));
    }
}
