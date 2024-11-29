package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Review;
import com.myproject.elearning.dto.request.review.ReviewCreateRequest;
import com.myproject.elearning.dto.request.review.ReviewUpdateRequest;
import com.myproject.elearning.dto.response.review.ReviewResponse;
import com.myproject.elearning.dto.response.review.ReviewUpdateResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface ReviewMapper {
    Review toEntity(ReviewCreateRequest request);

    ReviewResponse toResponse(Review entity);

    ReviewUpdateResponse toUpdateResponse(Review entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Review entity, ReviewUpdateRequest request);
}
