package com.myproject.elearning.dto.request.discount;

import com.myproject.elearning.domain.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCreateRequest {
    private String discountName;
    private String discountDescription;
    private Discount.DiscountType discountType;
    private BigDecimal discountValue;
    private String discountCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUses;
    private Integer maxUsesPerUser;
    private BigDecimal minOrderValue;
    private Discount.DiscountAppliesTo appliesTo;
    private Set<Long> specificCourseIds;
}
