package com.myproject.elearning.repository;

import com.myproject.elearning.domain.GroupChat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    boolean existsByCourseId(Long courseId);

    Optional<GroupChat> findByCourseId(Long courseId);

    @Query("SELECT gc FROM GroupChat gc LEFT JOIN FETCH gc.messages WHERE gc.id = :id")
    Optional<GroupChat> findByIdWithMessages(@Param("id") Long id);
}
