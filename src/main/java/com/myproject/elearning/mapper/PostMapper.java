package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Post;
import com.myproject.elearning.dto.request.post.PostCreateReq;
import com.myproject.elearning.dto.request.post.PostUpdateReq;
import com.myproject.elearning.dto.response.post.PostAddRes;
import com.myproject.elearning.dto.response.post.PostGetRes;
import com.myproject.elearning.dto.response.post.PostListRes;
import com.myproject.elearning.dto.response.post.PostUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface PostMapper {
    Post toEntity(PostCreateReq request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    PostGetRes toGetResponse(Post entity);

    @Mapping(target = "userId", source = "user.id")
    PostAddRes toPostAddRes(Post entity);

    PostListRes toPostListRes(Post entity);

    PostUpdateRes toPostUpdateRes(Post entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Post entity, PostUpdateReq request);
}
