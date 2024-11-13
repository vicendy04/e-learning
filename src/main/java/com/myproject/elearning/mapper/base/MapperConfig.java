package com.myproject.elearning.mapper.base;

import org.mapstruct.NullValuePropertyMappingStrategy;

@org.mapstruct.MapperConfig(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperConfig {}
