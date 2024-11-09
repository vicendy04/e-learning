package com.myproject.elearning.dto.request;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentReorderRequest {
    private Map<Long, Integer> orderMapping;
}
