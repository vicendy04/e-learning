package com.myproject.elearning.dto.projection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuth {
    Long id;
    String email;
    String password;
    Set<String> roleNames;

    public UserAuth(Long id, String email, String password, String roleNames) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roleNames =
                roleNames != null ? Arrays.stream(roleNames.split(",")).collect(Collectors.toSet()) : new HashSet<>();
    }
}
