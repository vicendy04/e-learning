package com.myproject.elearning.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content_url")
    String contentUrl;

    @Column(name = "order_index")
    Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    LessonType contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    Chapter chapter;

    @Column(name = "is_free_preview")
    Boolean isFreePreview = false;

    public enum LessonType {
        VIDEO,
        DOCUMENT,
        QUIZ,
        ASSIGNMENT
    }
}
