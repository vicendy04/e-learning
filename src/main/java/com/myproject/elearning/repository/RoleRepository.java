package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Role} entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {}
