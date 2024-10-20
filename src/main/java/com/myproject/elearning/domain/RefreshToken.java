package com.myproject.elearning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A refresh token which associated with a user.
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The {@link MapsId} annotation indicates that the id of RefreshToken is the same as the id of the associated User.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    //    @Column(nullable = false)
    //    private Instant expiryDate;
}
