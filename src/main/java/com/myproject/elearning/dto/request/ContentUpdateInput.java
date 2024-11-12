package com.myproject.elearning.dto.request;

import com.myproject.elearning.domain.Content;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentUpdateInput {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Content.ContentType contentType;
    private Content.ContentStatus status;

    @NotNull
    private Long courseId;
}
