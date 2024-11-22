package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myproject.elearning.security.AuthoritiesConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A user.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
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

    @NotNull
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshToken = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_name", referencedColumnName = "name"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnoreProperties("user") // dùng dto
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Enrollment> enrollments = new ArrayList<>();

    // giúp gọi từ chiều này, không thêm thông tin trong db, và sẽ tối ưu hơn là set 1 chiều.
    @JsonIgnoreProperties("instructor")
    @OneToMany(mappedBy = "instructor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Course> instructedCourses = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setUser(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setUser(null);
    }

    public void addInstructedCourse(Course course) {
        if (this.roles.stream().noneMatch(role -> role.getName().equals(AuthoritiesConstants.INSTRUCTOR))) {
            throw new IllegalStateException("Only instructors can create courses");
        }
        instructedCourses.add(course);
        course.setInstructor(this);
    }

    public void removeInstructedCourse(Course course) {
        instructedCourses.remove(course);
        course.setInstructor(null);
    }
}
