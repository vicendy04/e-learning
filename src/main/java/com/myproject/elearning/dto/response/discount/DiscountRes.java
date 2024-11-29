package com.myproject.elearning.dto.response.discount;

import com.myproject.elearning.domain.Discount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRes {
    Long id;
    String discountName;
    String discountDescription;
    Discount.DiscountType discountType;
    BigDecimal discountValue;
    String discountCode;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer maxUses;
    Integer usesCount;
    Integer maxUsesPerUser;
    BigDecimal minOrderValue;
    Boolean isActive;
    Discount.DiscountAppliesTo appliesTo;
    Set<Long> specificCourseIds;
}
