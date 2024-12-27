package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.auth.RegisterReq;
import com.myproject.elearning.dto.request.course.CourseCreateReq;
import com.myproject.elearning.dto.request.user.UserUpdateReq;
import com.myproject.elearning.dto.response.user.UserGetRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    Course toEntity(CourseCreateReq request);

    UserUpdateReq toUpdateResponse(User entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget User entity, UserUpdateReq request);

    RegisterReq toRegisterRequest(User entity);

    User toEntity(RegisterReq request);

    UserGetRes toGetResponse(User entity);
}
