package com.myproject.elearning.mapper.user;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.response.user.UserGetResponse;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserGetMapper extends EntityMapper<UserGetResponse, User> {}
