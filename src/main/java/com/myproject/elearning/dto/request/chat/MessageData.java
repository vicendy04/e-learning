package com.myproject.elearning.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageData implements Serializable {
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    String content;

    @NotNull(message = "ID nhóm chat không được để trống")
    String roomId;

    String senderId;
}
