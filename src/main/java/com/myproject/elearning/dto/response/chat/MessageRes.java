package com.myproject.elearning.dto.response.chat;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRes {
    String content;
    Long senderId;
    String senderName;
    LocalDateTime timestamp;
}
