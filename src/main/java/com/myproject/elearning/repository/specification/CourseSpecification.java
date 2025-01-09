package com.myproject.elearning.repository.specification;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.dto.request.course.CourseSearchDTO;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {
    public static Specification<Course> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Course> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            try {
                Course.CourseCategory courseCategory = Course.CourseCategory.valueOf(category.toUpperCase());
                return criteriaBuilder.equal(root.get("category"), courseCategory);
            } catch (IllegalArgumentException e) {
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Course> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }

            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Course> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")), "%" + keyword.toLowerCase() + "%"));
        };
    }

    public static Specification<Course> filterCourses(CourseSearchDTO searchDTO) {
        return Specification.where(hasTitle(searchDTO.getTitle()))
                .and(hasCategory(String.valueOf(searchDTO.getCategory())))
                .and(hasPriceRange(searchDTO.getMinPrice(), searchDTO.getMaxPrice()))
                .and(searchByKeyword(searchDTO.getKeyword()));
    }
}
