package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @Column(name = "title")
    private String title;

    @Column(name = "overview", columnDefinition = "MEDIUMTEXT")
    private String overview;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to create an empty course (without modules).
     * It also resolves the response issue for the "get module" API.
     * The {@link List<Module>} is used to avoid fetching the collection when add elements.
     */
    @JsonIgnoreProperties(
            value = {"course"},
            allowSetters = true)
    @OrderBy("order ASC") // auto-sorted list of modules based on their order
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();
}
