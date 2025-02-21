package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myproject.elearning.domain.enums.EnrollmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"enrollments", "refreshToken", "password"})
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties({"enrollments", "contents"})
    Course course;

    @Column(name = "enrolled_at", updatable = false)
    Instant enrolledAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "reason_for_dropping")
    String reasonForDropping;

    @PrePersist
    public void prePersist() {
        enrolledAt = Instant.now();
    }
}
