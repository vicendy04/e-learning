package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {

    Role toEntity(RoleDTO request);

    RoleDTO toDto(Role entity);
}
