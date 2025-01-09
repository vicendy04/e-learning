package com.myproject.elearning.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateReq {
    @Email(message = "Email không hợp lệ")
    String email; // Todo: làm otp

    @Size(max = 50)
    String firstName;

    @Size(max = 50)
    String lastName;

    String imageUrl;
}
