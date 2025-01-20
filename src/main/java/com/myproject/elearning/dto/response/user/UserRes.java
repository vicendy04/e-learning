package com.myproject.elearning.dto.response.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRes {
    Long id;
    String email;
    String username;
    String firstName;
    String lastName;
    String imageUrl;
}
