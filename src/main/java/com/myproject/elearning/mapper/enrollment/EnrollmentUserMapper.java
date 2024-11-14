package com.myproject.elearning.mapper.enrollment;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.response.enrollment.EnrollmentResponse.UserInfo;
import com.myproject.elearning.mapper.base.EntityMapper;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface EnrollmentUserMapper extends EntityMapper<UserInfo, User> {}
