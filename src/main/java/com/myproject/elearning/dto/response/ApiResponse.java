package com.myproject.elearning.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * A generic API response wrapper.
 *
 * @param <T> The type of the response data.
 */
@Getter
@Builder
public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
}
