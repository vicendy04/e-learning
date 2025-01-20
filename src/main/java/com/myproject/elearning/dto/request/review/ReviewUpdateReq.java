package com.myproject.elearning.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewUpdateReq {
    @Min(1)
    @Max(5)
    Integer rating;

    String comment;
}
