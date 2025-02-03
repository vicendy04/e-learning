package com.myproject.elearning.repository;

import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.domain.User;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository
        extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailById(@Param("userId") Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.preferences WHERE u.id = :userId")
    Optional<User> findWithPreferencesById(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteByUserId(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM user_preferences up WHERE up.user_id= :userId", nativeQuery = true)
    Long countUserPreferences(@Param("userId") Long userId);

    @Modifying
    @Query(
            value = "DELETE FROM user_preferences WHERE user_id = :userId AND topic_id IN (:topicIds)",
            nativeQuery = true)
    void delUserPreferences(@Param("userId") Long userId, @Param("topicIds") Set<Long> topicIds);

    @Query("SELECT t FROM Topic t JOIN t.interestedUsers u WHERE u.id = :userId")
    Set<Topic> getMyPreferences(@Param("userId") Long userId);

    @Query("SELECT t.id FROM Topic t JOIN t.interestedUsers u WHERE u.id = :userId")
    Set<Long> getMyPreferencesIds(@Param("userId") Long userId);

    default User getReferenceIfExists(Long id) {
        if (!existsById(id)) {
            throw new InvalidIdEx("Entity with ID " + id + " not found");
        }
        return getReferenceById(id);
    }
}
