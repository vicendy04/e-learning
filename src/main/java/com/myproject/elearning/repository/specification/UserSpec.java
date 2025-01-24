package com.myproject.elearning.repository.specification;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.request.user.UserSearchDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpec {
    public static Specification<User> hasRoles(String roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String[] roleArray = roles.split(",");
            return root.join("roles").get("name").in((Object[]) roleArray);
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<User> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("username")), "%" + keyword.toLowerCase() + "%"));
        };
    }

    public static Specification<User> filterUsers(UserSearchDTO searchDTO) {
        return Specification.where(hasRoles(searchDTO.getRoles()))
                .and(hasEmail(searchDTO.getEmail()))
                .and(hasUsername(searchDTO.getUsername()))
                .and(searchByKeyword(searchDTO.getKeyword()));
    }
}
