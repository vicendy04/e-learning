package com.myproject.elearning.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreateReq {
    @NotBlank(message = "Nội dung không được để trống")
    String content;
}
