package com.myproject.elearning.service.dto.request;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleReorderRequest {
    private Map<Long, Integer> orderMapping;
}
