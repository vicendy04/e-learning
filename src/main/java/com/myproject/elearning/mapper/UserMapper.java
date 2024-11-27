package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.auth.RegisterRequest;
import com.myproject.elearning.dto.request.course.CourseCreateRequest;
import com.myproject.elearning.dto.request.user.UserUpdateRequest;
import com.myproject.elearning.dto.response.user.UserGetResponse;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.*;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    // Create operations
    Course toEntity(CourseCreateRequest request);

    // Update operations

    UserUpdateRequest toUpdateResponse(User entity);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget User entity, UserUpdateRequest request);

    // Register operations
    RegisterRequest toRegisterRequest(User entity);

    User toEntity(RegisterRequest request);

    // Get operations
    UserGetResponse toGetResponse(User entity);

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
