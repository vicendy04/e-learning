package com.myproject.elearning.dto.response.chat;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRes {
    String content;
    Long senderId;
    String senderName;
    LocalDateTime timestamp;
}
