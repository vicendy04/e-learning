package com.myproject.elearning.dto.response.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentListResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private String contentType;
    private String status;
}
