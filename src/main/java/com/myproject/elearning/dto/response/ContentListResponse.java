package com.myproject.elearning.dto.response;

import com.myproject.elearning.domain.Content;
import lombok.Getter;

@Getter
public class ContentListResponse {
    private final Long id;
    private final String title;
    private final Integer orderIndex;
    private final Content.ContentType contentType;
    private final Content.ContentStatus status;

    public ContentListResponse(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.orderIndex = content.getOrderIndex();
        this.contentType = content.getContentType();
        this.status = content.getStatus();
    }
}
