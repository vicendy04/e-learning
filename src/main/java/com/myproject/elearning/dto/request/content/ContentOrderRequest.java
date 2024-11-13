package com.myproject.elearning.dto.request.content;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentOrderRequest {
    @NotNull
    private Map<Long, Integer> orderMapping;
}
