package com.myproject.elearning.dto.request.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupChatCreateReq {
    @NotBlank(message = "Tên nhóm chat không được để trống")
    String name;

    Long courseId;
}
