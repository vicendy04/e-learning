package com.myproject.elearning.dto.request.discount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplyDiscountReq {
    String discountCode;
    Long courseId;
}
