package com.myproject.elearning.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUpdateReq {
    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, message = "Nội dung phải ít nhất 10 ký tự")
    String content;
}
