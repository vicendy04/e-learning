package com.myproject.elearning.service.dto;

import com.myproject.elearning.domain.User;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String imageUrl;

    public static UserDTO from(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setImageUrl(user.getImageUrl());
        return userDTO;
    }
}
