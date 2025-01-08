package com.myproject.elearning.dto.response.enrollment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {
    Long id;
    String email;
    String username;
}
