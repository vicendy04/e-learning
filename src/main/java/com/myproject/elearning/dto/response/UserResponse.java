package com.myproject.elearning.dto.response;

import com.myproject.elearning.domain.Role;
import com.myproject.elearning.domain.User;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String username;
    private String imageUrl;
    private Set<Role> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.imageUrl = user.getImageUrl();
        this.roles = user.getRoles();
    }

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setUsername(user.getUsername());
        userResponse.setImageUrl(user.getImageUrl());
        userResponse.setRoles(user.getRoles());
        return userResponse;
    }
}
