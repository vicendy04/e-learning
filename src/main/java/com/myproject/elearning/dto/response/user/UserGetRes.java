package com.myproject.elearning.dto.response.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGetRes {
    Long id;
    String email;
    String username;
    String imageUrl;
    //    Set<Role> roles;
}
