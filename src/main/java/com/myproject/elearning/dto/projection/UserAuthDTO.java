package com.myproject.elearning.dto.projection;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
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
    Set<String> roleNames;

    public UserAuthDTO() {}

    public UserAuthDTO(Long id, String email, String password, String roleNames) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roleNames = Arrays.stream(roleNames.split(",")).collect(Collectors.toSet());
    }
}
