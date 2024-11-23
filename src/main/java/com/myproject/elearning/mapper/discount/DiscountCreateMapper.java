package com.myproject.elearning.mapper.discount;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.request.discount.DiscountCreateRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.ArrayList;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapperConfig.class,
        imports = {ArrayList.class, Collections.class})
public interface DiscountCreateMapper extends EntityMapper<DiscountCreateRequest, Discount> {

    @Mapping(target = "id", ignore = true) // fix DiscountCreateMapperImpl
    //    @Mapping(target = "instructorId", source = "instructorId")
    @Mapping(
            target = "specificCourseIds",
            expression =
                    "java(dto.getAppliesTo() == Discount.DiscountAppliesTo.SPECIFIC ? new ArrayList<>(dto.getSpecificCourseIds()) : Collections.EMPTY_LIST)")
    Discount toEntity(DiscountCreateRequest dto, Long instructorId);
}
