package com.myproject.elearning.repository;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.projection.UserInfo;
import com.myproject.elearning.exception.problemdetails.InvalidIdEx;
import java.util.Optional;
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
    default User getReferenceIfExists(Long id) {
        if (!existsById(id)) {
            throw new InvalidIdEx("Entity with ID " + id + " not found");
        }
        return getReferenceById(id);
    }

    @Query(
            """
			SELECT new com.myproject.elearning.dto.projection.UserInfo(u.id, u.email, u.username, u.firstName, u.lastName)
			FROM User u
			WHERE u.id = :id
			""")
    Optional<UserInfo> findUserInfo(@Param("id") Long id);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailByUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteByUserId(@Param("id") Long id);
}
