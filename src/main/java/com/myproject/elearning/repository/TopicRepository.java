package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Topic;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Set<Topic> findAllByIdIn(Collection<Long> ids);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.id IN :ids")
    long countByIdIn(@Param("ids") Collection<Long> ids);

    @Query(value = "SELECT COUNT(*) FROM user_preferences up WHERE up.user_id= :userId", nativeQuery = true)
    Long countUserPreferences(@Param("userId") Long userId);
}
