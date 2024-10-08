package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Course} entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {}
