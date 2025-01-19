package com.myproject.elearning.mapper.base;

import org.mapstruct.NullValuePropertyMappingStrategy;

@org.mapstruct.MapperConfig(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperConfig {}
