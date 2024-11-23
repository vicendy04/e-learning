package com.myproject.elearning.dto.request.discount;

import com.myproject.elearning.domain.Discount;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCreateRequest {
    @NotBlank
    private String discountName;

    private String discountDescription;

    @NotNull
    private Discount.DiscountType discountType;

    @NotNull
    private BigDecimal discountValue;

    @NotBlank
    private String discountCode;

    @FutureOrPresent
    private LocalDateTime startDate;

    @Future
    private LocalDateTime endDate;

    @NotNull(message = "Max uses is required")
    @Min(value = 1, message = "Max uses must be at least 1")
    private Integer maxUses;

    @NotNull(message = "Max uses per user is required")
    @Min(value = 1, message = "Max uses per user must be at least 1")
    private Integer maxUsesPerUser;

    private BigDecimal minOrderValue;
    private Boolean isActive;
    private Discount.DiscountAppliesTo appliesTo;
    private List<Long> specificCourseIds;
}
