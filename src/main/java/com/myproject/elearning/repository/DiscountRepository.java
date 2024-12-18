package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Discount;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {
    boolean existsByInstructorIdAndDiscountCode(Long id, String discountCode);

    Page<Discount> findAllByInstructorId(Pageable pageable, Long instructorId);

    @Modifying
    @Query("DELETE FROM Discount d WHERE d.instructorId = :instructorId AND d.id= :discountId")
    int deleteByIdAndInstructorId(@Param("discountId") Long discountId, @Param("instructorId") Long instructorId);

    // specification
    Page<Discount> findAllByIsActiveTrue(Pageable pageable);

    @Query("SELECT d.specificCourseIds FROM Discount d WHERE d.id = :discountId")
    Set<Long> findSpecificCourseIdsByDiscountId(@Param("discountId") Long discountId);

    @EntityGraph(attributePaths = {"specificCourseIds", "instructorId"})
    Optional<Discount> findByDiscountCode(@Param("discountCode") String discountCode);

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
