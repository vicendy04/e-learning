package com.myproject.elearning.mapper.discount;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.dto.response.discount.DiscountGetResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.Collections;
import java.util.HashSet;
import org.mapstruct.Mapper;

@Mapper(
        config = MapperConfig.class,
        imports = {HashSet.class, Collections.class})
public interface DiscountGetMapper extends EntityMapper<DiscountGetResponse, Discount> {}
