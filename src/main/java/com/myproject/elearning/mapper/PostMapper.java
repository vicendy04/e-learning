package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface PostMapper {
    PostMapper POST_MAPPER = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    PostGetRes toGetRes(Post entity);

    Post toEntity(PostCreateReq request);

    PostListRes toListRes(Post entity);

    PostUpdateRes toUpdateRes(Post entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Post entity, PostUpdateReq request);
}
