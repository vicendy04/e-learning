package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A user.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Size(max = 50)
    @Column(name = "username", length = 50)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "image_url")
    private String imageUrl;
}
