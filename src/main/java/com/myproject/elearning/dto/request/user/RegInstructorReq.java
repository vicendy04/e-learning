package com.myproject.elearning.dto.request.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegInstructorReq {
    String firstName;
    String lastName;
    String cvUrl;
}
