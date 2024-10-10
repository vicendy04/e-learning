package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @Column(name = "overview", columnDefinition = "MEDIUMTEXT")
    private String overview;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to create an empty course (without modules).
     * It also resolves the response issue for the "get module" API.
     */
    @JsonIgnoreProperties(
            value = {"course"},
            allowSetters = true)
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Module> modules;
}
