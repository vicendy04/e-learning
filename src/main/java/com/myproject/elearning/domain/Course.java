package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A course.
 */
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "duration")
    private int duration;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private CourseCategory category;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to create an empty course (without contents).
     * It also resolves the response issue for the "get contents" API.
     * The {@link List<Content>} is used to avoid fetching the collection when adding elements.
     */
    @JsonIgnoreProperties(
            value = {"course"},
            allowSetters = true)
    @OrderBy("orderIndex ASC")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "course", orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();

    @JsonIgnoreProperties("course")
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    //    @JsonIgnoreProperties("courses")
    //    @ManyToMany(mappedBy = "specificCourses")
    //    private Set<Discount> discounts = new HashSet<>();

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

    public void addContent(Content content) {
        contents.add(content);
        content.setCourse(this);
    }

    public void removeContent(Content content) {
        contents.remove(content);
        content.setCourse(null);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setCourse(null);
    }
}
