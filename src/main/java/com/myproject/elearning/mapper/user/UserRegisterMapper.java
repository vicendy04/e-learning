package com.myproject.elearning.mapper.user;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.auth.RegisterRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserRegisterMapper extends EntityMapper<RegisterRequest, User> {}
