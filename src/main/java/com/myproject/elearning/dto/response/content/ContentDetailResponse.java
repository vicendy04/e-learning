package com.myproject.elearning.dto.response.content;

import com.myproject.elearning.domain.Content;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentDetailResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Content.ContentType contentType;
    private Content.ContentStatus status;

    public static ContentDetailResponse from(Content content) {
        ContentDetailResponse response = new ContentDetailResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setOrderIndex(content.getOrderIndex());
        response.setContentType(content.getContentType());
        response.setStatus(content.getStatus());
        return response;
    }
}
