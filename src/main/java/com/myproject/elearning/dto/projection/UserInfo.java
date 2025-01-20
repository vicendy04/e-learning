package com.myproject.elearning.dto.projection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {
    Long id;
    String email;
    String username;
    String firstName;
    String lastName;

    public UserInfo(Long id, String email, String username, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
