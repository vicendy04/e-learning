package com.myproject.elearning.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO {
    private String roles;
    private String email;
    private String username;
    private String keyword;
}
