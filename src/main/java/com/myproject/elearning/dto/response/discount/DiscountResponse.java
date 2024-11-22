package com.myproject.elearning.dto.response.discount;

import com.myproject.elearning.domain.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private Long id;
    private String discountName;
    private String discountDescription;
    private Discount.DiscountType discountType;
    private BigDecimal discountValue;
    private String discountCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUses;
    private Integer usesCount;
    private Integer maxUsesPerUser;
    private BigDecimal minOrderValue;
    private Boolean isActive;
    private Discount.DiscountAppliesTo appliesTo;
    private Set<Long> specificCourseIds;
}