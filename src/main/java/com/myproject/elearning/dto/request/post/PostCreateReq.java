package com.myproject.elearning.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreateReq {
    @NotBlank(message = "Nội dung không được để trống")
    String content;
}
