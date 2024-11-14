package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Course} entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    @Query("SELECT c FROM Course c ORDER BY c.title")
    Page<Course> findAllCourses(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.contents WHERE c.id IN :ids ORDER BY c.title")
    List<Course> findCoursesWithContents(List<Long> ids);

    @EntityGraph(attributePaths = "contents")
    Optional<Course> findById(Long id);

    Optional<Course> findLazyById(Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.contents ORDER BY c.title")
    Page<Course> findAllWithContents(Pageable pageable);

    // Thêm method để lấy courses theo category
    //    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.contents " +
    //            "WHERE c.category = :category")
    //    List<Course> findByCategoryWithContents(@Param("category") String category);

    // Thêm method để lấy courses với số lượng contents
    //    @Query("SELECT c, COUNT(cnt) FROM Course c " +
    //            "LEFT JOIN c.contents cnt " +
    //            "GROUP BY c.id")
    //    List<Object[]> findAllWithContentCount();

    // Tối ưu cho tìm kiếm
    @Query("SELECT c FROM Course c " + "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);
}
