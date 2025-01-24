package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myproject.elearning.constant.RegistrationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * A user.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email", unique = true)
    String email;

    @Size(max = 50)
    @Column(name = "username", length = 50)
    String username;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @NotNull
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "cv_url")
    String cvUrl;

    @Column(name = "registration_status")
    @Enumerated(EnumType.STRING)
    RegistrationStatus registrationStatus;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<RefreshToken> refreshToken = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @JsonIgnoreProperties(
            value = {"users"},
            allowSetters = true)
    Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_preferences",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    @JsonIgnoreProperties(
            value = {"interestedUsers"},
            allowSetters = true)
    Set<Topic> preferences = new HashSet<>();

    @JsonIgnoreProperties("user")
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Enrollment> enrollments = new ArrayList<>();

    @JsonIgnoreProperties("instructor")
    @OneToMany(
            mappedBy = "instructor",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Course> instructedCourses = new ArrayList<>();

    @JsonIgnoreProperties("user")
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Review> reviews = new ArrayList<>();

    @JsonIgnoreProperties("likedUsers")
    @ManyToMany(mappedBy = "likedUsers", fetch = FetchType.LAZY)
    Set<Post> likedPosts = new HashSet<>();
}
