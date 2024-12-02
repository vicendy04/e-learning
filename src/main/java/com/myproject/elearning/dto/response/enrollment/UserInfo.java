package com.myproject.elearning.dto.response.enrollment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {
    Long id;
    String email;
    String username;
}
