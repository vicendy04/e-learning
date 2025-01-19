package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    RoleMapper ROLE_MAPPER = Mappers.getMapper(RoleMapper.class);

    Role toEntity(RoleDTO request);

    RoleDTO toDto(Role entity);
}
