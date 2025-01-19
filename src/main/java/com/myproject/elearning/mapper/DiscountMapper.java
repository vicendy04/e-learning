package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.request.discount.DiscountCreateReq;
import com.myproject.elearning.dto.response.discount.DiscountRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.Collections;
import java.util.HashSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        config = MapperConfig.class,
        imports = {HashSet.class, Collections.class})
public interface DiscountMapper {
    DiscountMapper DISCOUNT_MAPPER = Mappers.getMapper(DiscountMapper.class);

    @Mapping(target = "id", ignore = true) // fix DiscountCreateMapperImpl
    @Mapping(
            target = "specificCourseIds",
            expression =
                    "java(dto.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC ? new HashSet<>(dto.getSpecificCourseIds()) : Collections.EMPTY_SET)")
    Discount toEntity(DiscountCreateReq dto, Long instructorId);

    DiscountRes toRes(Discount entity);
}
