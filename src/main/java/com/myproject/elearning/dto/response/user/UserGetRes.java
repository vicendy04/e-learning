package com.myproject.elearning.dto.response.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGetRes {
    Long id;
    String email;
    String username;
    String imageUrl;
    //    Set<Role> roles;
}
