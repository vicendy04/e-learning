package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * A user.
 */
@Getter
@Setter
@NoArgsConstructor
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

    @NotNull
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    @Column(name = "password", nullable = false)
    String password;

    @Column(name = "image_url")
    String imageUrl;

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

    @JsonIgnoreProperties("user") // dùng dto
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Enrollment> enrollments = new ArrayList<>();

    // giúp gọi từ chiều này, không thêm thông tin trong db, và sẽ tối ưu hơn là set 1 chiều.
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
