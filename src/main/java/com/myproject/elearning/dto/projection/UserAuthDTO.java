package com.myproject.elearning.dto.projection;

import java.io.Serializable;
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
    Set<String> roleNames;
}
