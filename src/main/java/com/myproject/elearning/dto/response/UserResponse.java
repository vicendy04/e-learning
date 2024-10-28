package com.myproject.elearning.dto.response;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String imageUrl;
    private Set<Role> roles;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .imageUrl(user.getImageUrl())
                .roles(user.getRoles())
                .build();
    }
}
