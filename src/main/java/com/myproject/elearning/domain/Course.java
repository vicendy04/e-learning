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
    int duration;

    @Column(name = "price", precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "category")
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
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Chapter> chapters = new ArrayList<>();

    public enum CourseCategory {
        FITNESS,
        DESIGN,
        PHOTOGRAPHY,
        MARKETING,
        PROGRAMMING,
        MUSIC,
        LANGUAGE_LEARNING,
        PERSONAL_DEVELOPMENT,
        FINANCE,
        COOKING
    }

    public enum Level {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        ALL_LEVELS
    }
}
