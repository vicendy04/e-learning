package com.myproject.elearning.mapper.user;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.user.UserUpdateRequest;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserUpdateMapper extends EntityMapper<UserUpdateRequest, User> {}
