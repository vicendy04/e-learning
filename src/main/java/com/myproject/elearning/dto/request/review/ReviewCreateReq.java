package com.myproject.elearning.dto.request.review;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreateReq {
    @NotNull(message = "Đánh giá sao không được để trống")
    @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
    Integer rating;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    @Size(min = 10, message = "Nội dung đánh giá phải có ít nhất 10 ký tự")
    String comment;
}
