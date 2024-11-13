package com.myproject.elearning.dto.response.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentGetResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private String contentType;
    private String status;
    private Long courseId;
    private String courseTitle;
}
