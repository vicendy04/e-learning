package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Review;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewAddRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    ReviewMapper REVIEW_MAPPER = Mappers.getMapper(ReviewMapper.class);

    Review toEntity(ReviewCreateReq request);

    ReviewAddRes toAddRes(Review entity);

    ReviewUpdateRes toUpdateRes(Review entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Review entity, ReviewUpdateReq request);
}
