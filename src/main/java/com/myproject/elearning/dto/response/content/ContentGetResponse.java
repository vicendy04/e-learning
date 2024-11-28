package com.myproject.elearning.dto.response.content;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentGetResponse {
    Long id;
    String title;
    Integer orderIndex;
    String contentType;
    String status;
    Long courseId;
    String courseTitle;
}
