package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * A course.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(name = "duration")
    Integer duration;

    @Column(name = "price", precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "category", length = 50)
    @Enumerated(EnumType.STRING)
    CourseCategory category;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    Level level;

    @Column(name = "enrolled_count")
    Integer enrolledCount = 0;

    @JsonIgnoreProperties("course")
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    List<Enrollment> enrollments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    User instructor;

    @JsonIgnoreProperties("course")
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Review> reviews = new ArrayList<>();

    @JsonIgnoreProperties("course")
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    List<Chapter> chapters = new ArrayList<>();

    @Column(name = "thumbnail_url")
    String thumbnailUrl;

    public enum CourseCategory {
        DEVELOPMENT,
        DESIGN,
        BUSINESS,
        MARKETING,
        SECURITY,
        FINANCE,
        COOKING,
    }

    public enum Level {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        ALL_LEVELS
    }
}
