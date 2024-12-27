package com.myproject.elearning.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "discount_name", nullable = false)
    String discountName;

    @Column(name = "discount_description", nullable = false)
    String discountDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    DiscountType discountType = DiscountType.FIXED_AMOUNT;

    @Column(name = "discount_value", nullable = false)
    BigDecimal discountValue;

    @Column(name = "discount_code", unique = true, nullable = false)
    String discountCode;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Column(name = "max_uses", nullable = false)
    Integer maxUses;

    @Column(name = "uses_count", nullable = false)
    Integer usesCount = 0;

    @Column(name = "max_uses_per_user", nullable = false)
    Integer maxUsesPerUser;

    @Column(name = "min_order_value", nullable = false)
    BigDecimal minOrderValue;

    @Column(name = "is_active")
    Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false)
    DiscountAppliesTo appliesTo = DiscountAppliesTo.ALL;

    @Column(name = "instructor_id", nullable = false)
    Long instructorId;

    @ElementCollection
    @CollectionTable(name = "discount_specific_course_ids", joinColumns = @JoinColumn(name = "discount_id"))
    @Column(name = "course_id")
    Set<Long> specificCourseIds = new HashSet<>();

    public enum DiscountType {
        FIXED_AMOUNT,
        PERCENTAGE
    }

    public enum DiscountAppliesTo {
        ALL,
        SPECIFIC
    }

    @PrePersist
    @PreUpdate
    private void validateDiscount() {
        if (appliesTo == DiscountAppliesTo.SPECIFIC && specificCourseIds.isEmpty()) {
            throw new IllegalStateException(
                    "Specific course IDs must be set when discount applies to specific courses");
        }
    }

    // don't use
    public BigDecimal getDiscountAmount(BigDecimal originalPrice) {
        if (this.discountType == Discount.DiscountType.FIXED_AMOUNT) {
            return getDiscountValue();
        } else {
            return originalPrice.multiply(getDiscountValue().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        }
    }
}
