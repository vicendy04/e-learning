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

    // Create operations
    Course toEntity(CourseCreateReq request);

    // Update operations

    UserUpdateReq toUpdateResponse(User entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget User entity, UserUpdateReq request);

    // Register operations
    RegisterReq toRegisterRequest(User entity);

    User toEntity(RegisterReq request);

    // Get operations
    UserGetRes toGetResponse(User entity);

    // List operations

    //    @Named("mapContents")
    //    default List<CourseGetResponse.ContentDTO> mapContents(List<Content> contents) {
    //        if (contents == null) {
    //            return Collections.emptyList();
    //        }
    //        List<CourseGetResponse.ContentDTO> contentDTOs = new ArrayList<>();
    //        for (Content content : contents) {
    //            contentDTOs.add(new CourseGetResponse.ContentDTO(
    //                    content.getId(),
    //                    content.getTitle(),
    //                    content.getOrderIndex(),
    //                    content.getStatus().name()));
    //        }
    //        return contentDTOs;
    //    }
}
