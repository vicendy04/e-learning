package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Module;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Module} entity.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>, JpaSpecificationExecutor<Module> {
    List<Module> findByCourseId(Long courseId);

    Page<Module> findByCourseId(Long courseId, Pageable pageable);
}
