package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Module} entity.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {}
