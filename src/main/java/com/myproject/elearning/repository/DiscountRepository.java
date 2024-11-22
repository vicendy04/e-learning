package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Discount;
import com.myproject.elearning.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    boolean existsByDiscountCode(String discountCode);

    Optional<Discount> findByDiscountCode(String discountCode);

    Optional<Discount> findByDiscountCodeAndInstructor(String discountCode, User instructor);

    Page<Discount> findAllByInstructor(User instructor, Pageable pageable);

    Page<Discount> findAllByIsActiveTrue(Pageable pageable);
}