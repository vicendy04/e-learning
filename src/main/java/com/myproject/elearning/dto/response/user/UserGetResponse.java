package com.myproject.elearning.dto.response.user;

import com.myproject.elearning.domain.Role;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResponse {
    private Long id;
    private String email;
    private String username;
    private String imageUrl;
//    private Set<Role> roles;
}
