package com.myproject.elearning.service.mapper;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.service.dto.response.UserResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link User} and its DTO called {@link UserResponse}.
 * <p>
 * Normal mappers are generated using MapStruct, this one is hand-coded as MapStruct
 * support is still in beta, and requires a manual step with an IDE.
 */
@Service
public class UserMapper {
    public List<UserResponse> usersToUserDTOs(List<User> users) {
        return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).toList();
    }

    public UserResponse userToUserDTO(User user) {
        return new UserResponse(user);
    }
}
