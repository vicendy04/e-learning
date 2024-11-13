package com.myproject.elearning.mapper.role;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.dto.response.role.RoleDTO;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {
}
