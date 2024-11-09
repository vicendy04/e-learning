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
    private String category;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to create an empty course (without contents).
     * It also resolves the response issue for the "get contents" API.
     * The {@link List<Content>} is used to avoid fetching the collection when adding elements.
     */
    @JsonIgnoreProperties(
            value = {"course"},
            allowSetters = true)
    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents = new ArrayList<>();
}
