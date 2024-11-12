package com.myproject.elearning.dto.mapper;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.RegisterInput;
import com.myproject.elearning.dto.response.UserResponse;
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
    public List<UserResponse> toUserDTOList(List<User> users) {
        return users.stream().filter(Objects::nonNull).map(this::toUserDTO).toList();
    }

    public UserResponse toUserDTO(User user) {
        return UserResponse.from(user);
    }

    public User registerRequestToUser(RegisterInput registerInput) {
        return new User(registerInput.getEmail(), registerInput.getUsername(), registerInput.getPassword());
    }
}
