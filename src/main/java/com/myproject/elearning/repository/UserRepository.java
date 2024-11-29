package com.myproject.elearning.repository;

import com.myproject.elearning.domain.User;
import com.myproject.elearning.dto.auth.UserAuthDTO;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    default User getReferenceIfExists(Long id) {
        if (!existsById(id)) {
            throw new InvalidIdException("Entity with ID " + id + " not found");
        }
        return getReferenceById(id);
    }

    Optional<User> findByEmail(String username);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.myproject.elearning.dto.auth.UserAuthDTO(" + "u.id, "
            + "u.email, "
            + "u.password, "
            + "r.name) "
            + "FROM User u "
            + "JOIN u.roles r "
            + "WHERE u.email = :email")
    Optional<UserAuthDTO> findAuthDTOByEmail(@Param("email") String email);
}
