package com.myproject.elearning.dto.request.discount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDiscountRequest {
    private String discountCode;
    private Long courseId;
}
