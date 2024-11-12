package com.myproject.elearning.dto.request;

import com.myproject.elearning.domain.User;
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
public class UserUpdateInput {
    private Long id;
    private String email;
    private String username;
    private String imageUrl;

    public UserUpdateInput(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.imageUrl = user.getImageUrl();
    }

    public static UserUpdateInput from(User user) {
        UserUpdateInput userResponse = new UserUpdateInput();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setUsername(user.getUsername());
        userResponse.setImageUrl(user.getImageUrl());
        return userResponse;
    }
}
