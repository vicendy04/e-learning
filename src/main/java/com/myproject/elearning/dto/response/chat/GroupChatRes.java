package com.myproject.elearning.dto.response.chat;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupChatRes {
    Long id;
    String name;
    Long courseId;
    LocalDateTime createdAt;
}
