package com.myproject.elearning.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserAuthDTO implements Serializable {
    private Long id;
    private String email;
    private String password;
    private Set<String> roles;

    public UserAuthDTO() {
    }

    public UserAuthDTO(Long id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = new HashSet<>();
        this.roles.add(role);
    }
}