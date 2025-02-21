package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Discount;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {
    boolean existsByInstructorIdAndDiscountCode(Long id, String discountCode);

    Page<Discount> findAllByInstructorId(Long instructorId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"specificCourseIds"})
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :discountCode")
    Optional<Discount> findByDiscountCodeWithLock(@Param("discountCode") String discountCode);

    @Query("SELECT d.instructorId FROM Discount d WHERE d.id = :disCountId")
    Optional<Long> findInstructorIdByCourseId(@Param("disCountId") Long disCountId);

    @Query(
            value =
                    """
							SELECT d.id as id,
							d.is_active as isActive,
							d.start_date as startDate,
							d.end_date as endDate,
							d.uses_count as usesCount,
							d.max_uses as maxUses,
							d.min_order_value as minOrderValue,
							d.instructor_id as instructorId,
							d.applies_to as appliesTo,
							JSON_ARRAYAGG(sci.course_id) as specificCourseIds
							FROM discounts d
							LEFT JOIN discount_specific_course_ids sci ON d.id = sci.discount_id
							WHERE d.discount_code = :discountCode
							GROUP BY d.id, d.is_active, d.start_date, d.end_date, d.uses_count, d.max_uses, d.min_order_value, d.instructor_id, d.applies_to
							""",
            nativeQuery = true)
    Optional<DiscountValidation> findActiveDiscountByCode(@Param("discountCode") String discountCode);

    interface DiscountValidation {
        Long getId();

        Boolean getIsActive();

        LocalDateTime getStartDate();

        LocalDateTime getEndDate();

        Integer getUsesCount();

        Integer getMaxUses();

        BigDecimal getMinOrderValue();

        Long getInstructorId();

        Discount.DiscountAppliesTo getAppliesTo();

        String getSpecificCourseIds();
    }
}
