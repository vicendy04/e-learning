package com.myproject.elearning.repository.specification;

import com.myproject.elearning.domain.Course;
import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.dto.search.CourseFilters;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CourseSpec {
    public static Specification<Course> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
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

    public static Specification<Course> hasTopics(Set<Long> topicIds) {
        return (root, query, criteriaBuilder) -> {
            if (topicIds == null || topicIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<Course, Topic> topicJoin = root.join("topic", JoinType.INNER);
            return topicJoin.get("id").in(topicIds);
        };
    }

    public static Specification<Course> filterCourses(CourseFilters filters) {
        return Specification.where(hasTopics(filters.getTopicIds()));
    }
}
