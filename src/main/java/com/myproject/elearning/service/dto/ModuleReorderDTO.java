package com.myproject.elearning.service.dto;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleReorderDTO {
    private Map<Long, Integer> orderMapping;
}
