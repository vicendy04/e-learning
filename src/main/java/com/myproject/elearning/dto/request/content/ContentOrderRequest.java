package com.myproject.elearning.dto.request.content;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentOrderRequest {
    @NotNull
    Map<Long, Integer> orderMapping;
}
