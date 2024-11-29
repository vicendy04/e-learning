package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Review;
import com.myproject.elearning.dto.request.review.ReviewCreateReq;
import com.myproject.elearning.dto.request.review.ReviewUpdateReq;
import com.myproject.elearning.dto.response.review.ReviewRes;
import com.myproject.elearning.dto.response.review.ReviewUpdateRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    Review toEntity(ReviewCreateReq request);

    ReviewRes toResponse(Review entity);

    ReviewUpdateRes toUpdateResponse(Review entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Review entity, ReviewUpdateReq request);
}
