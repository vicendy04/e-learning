package com.myproject.elearning.dto.auth;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuthDTO implements Serializable {
    Long id;
    String email;
    String password;
    Set<String> roles;

    public UserAuthDTO() {}

    public UserAuthDTO(Long id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
        this.roles.add(role);
    }
}
