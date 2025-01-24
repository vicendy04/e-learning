package com.myproject.elearning.dto.response.user;

import com.myproject.elearning.constant.RegistrationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegInstructorRes {
    RegistrationStatus registrationStatus;
}
