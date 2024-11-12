package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"enrollments", "refreshToken", "password"}) // dùng dto
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties({"enrollments", "contents"}) // dùng dto
    private Course course;

    @Column(name = "enrolled_at")
    private Instant enrolledAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    public enum EnrollmentStatus {
        ACTIVE,
        COMPLETED,
        DROPPED
    }

    @PrePersist
    public void prePersist() {
        enrolledAt = Instant.now();
    }
}
