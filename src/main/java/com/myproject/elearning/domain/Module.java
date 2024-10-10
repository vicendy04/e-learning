package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * A module.
 */
@Getter
@Setter
@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @PositiveOrZero
    @Column(name = "module_order")
    private Integer order;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to resolve the response issue for the "get module" API.
     */
    @JsonIgnoreProperties(
            value = {"modules"},
            allowSetters = true)
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    //    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    //    private Set<Content> contents;
}
