package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Content;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Content} entity.
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {
    List<Content> findByCourseId(Long courseId);

    Page<Content> findByCourseId(Long courseId, Pageable pageable);
}
