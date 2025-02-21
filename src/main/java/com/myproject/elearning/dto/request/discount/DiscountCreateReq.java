package com.myproject.elearning.dto.request.discount;

import com.myproject.elearning.domain.Discount;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCreateReq {
    @NotBlank
    String discountName;

    String discountDescription;

    @NotNull
    Discount.DiscountType discountType;

    @NotNull
    BigDecimal discountValue;

    @NotBlank
    String discountCode;

    @FutureOrPresent
    LocalDateTime startDate;

    @Future
    LocalDateTime endDate;

    @NotNull(message = "Max uses is required")
    @Min(value = 1, message = "Max uses must be at least 1")
    Integer maxUses;

    @NotNull(message = "Max uses per user is required")
    @Min(value = 1, message = "Max uses per user must be at least 1")
    Integer maxUsesPerUser;

    BigDecimal minOrderValue;
    Boolean isActive;
    Discount.DiscountAppliesTo appliesTo;
    Set<Long> specificCourseIds;

    @AssertTrue(message = "Specific course IDs must not be empty when applies to SPECIFIC")
    private boolean isValidSpecificCourses() {
        if (appliesTo == Discount.DiscountAppliesTo.SPECIFIC) {
            return specificCourseIds != null && !specificCourseIds.isEmpty();
        }
        return true;
    }
}
