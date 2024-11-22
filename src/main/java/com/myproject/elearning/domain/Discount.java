package com.myproject.elearning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    @Column(name = "discount_description", nullable = false)
    private String discountDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType = DiscountType.FIXED_AMOUNT;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "discount_code", unique = true, nullable = false)
    private String discountCode;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "max_uses", nullable = false)
    private Integer maxUses;

    @Column(name = "uses_count", nullable = false)
    private Integer usesCount = 0;

    @Column(name = "max_uses_per_user", nullable = false)
    private Integer maxUsesPerUser;

    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false)
    private DiscountAppliesTo appliesTo = DiscountAppliesTo.ALL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ElementCollection
    @CollectionTable(
            name = "discount_specific_course_ids",
            joinColumns = @JoinColumn(name = "discount_id")
    )
    @Column(name = "course_id")
    private Set<Long> specificCourseIds = new HashSet<>();

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
            throw new IllegalStateException("Specific course IDs must be set when discount applies to specific courses");
        }
    }

    public boolean isApplicableToCourse(Course course) {
        if (!isActive || LocalDateTime.now().isBefore(startDate) || LocalDateTime.now().isAfter(endDate)) {
            return false;
        }

        if (usesCount >= maxUses) {
            return false;
        }

        // Kiểm tra xem khóa học có thuộc instructor này không
        if (!course.getInstructor().equals(this.instructor)) {
            return false;
        }

        // Nếu là ALL thì áp dụng cho tất cả khóa học của instructor
        if (appliesTo == DiscountAppliesTo.ALL) {
            return true;
        }

        // Nếu là SPECIFIC thì kiểm tra xem ID khóa học có nằm trong danh sách không
        return specificCourseIds.contains(course.getId());
    }

} 