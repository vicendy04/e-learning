package com.myproject.elearning.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * A content.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @PositiveOrZero
    @Column(name = "order_index")
    Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    ContentType contentType;

    // URL for video or document content
    // @Column(name = "content_url")
    // String contentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_status")
    ContentStatus status = ContentStatus.DRAFT;

    /**
     * The {@link JsonIgnoreProperties} annotation is used to resolve the response issue for the "get content" API.
     */
    @JsonIgnoreProperties(
            value = {"contents"},
            allowSetters = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;

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
