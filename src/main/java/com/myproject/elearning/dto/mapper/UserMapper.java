package com.myproject.elearning.dto.mapper;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.RegisterRequest;
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
    public List<UserResponse> usersToUserDTOs(List<User> users) {
        return users.stream()
                .filter(Objects::nonNull)
                .map(this::userToUserResponse)
                .toList();
    }

    public UserResponse userToUserResponse(User user) {
        return new UserResponse(user);
    }

    public User registerRequestToUser(RegisterRequest registerRequest) {
        return new User(registerRequest.getEmail(), registerRequest.getUsername(), registerRequest.getPassword());
    }
}
