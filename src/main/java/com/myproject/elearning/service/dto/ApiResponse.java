package com.myproject.elearning.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A generic API response wrapper.
 *
 * @param <T> The type of the response data.
 */
@Getter
@Setter
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
}
