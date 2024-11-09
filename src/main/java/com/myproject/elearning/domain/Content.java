package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * A content.
 */
@Getter
@Setter
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @PositiveOrZero
    @Column(name = "order_index")
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    // URL for video or document content
    // @Column(name = "content_url")
    // private String contentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status")
    private ContentStatus status = ContentStatus.DRAFT;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to resolve the response issue for the "get content" API.
     */
    @JsonIgnoreProperties(
            value = {"contents"},
            allowSetters = true)
    @ManyToOne()
    @JoinColumn(name = "course_id")
    private Course course;

    public enum ContentType {
        ASSIGNMENT,
        CODE_EXERCISE,
        DOCUMENT,
        PRESENTATION,
        QUIZ,
        VIDEO
    }

    public enum ContentStatus {
        PUBLISHED,
        DRAFT
    }
}
