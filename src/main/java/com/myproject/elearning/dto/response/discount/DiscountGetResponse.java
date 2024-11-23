package com.myproject.elearning.dto.response.discount;

import com.myproject.elearning.domain.Discount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountGetResponse {
    private Long id;
    private String discountName;
    private String discountDescription;
    private Discount.DiscountType discountType;
    private BigDecimal discountValue;
    private String discountCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minOrderValue;
}
