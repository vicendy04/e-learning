package com.myproject.elearning.dto.common;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * A generic API response wrapper.
 *
 * @param <T> The type of the response data.
 */
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    Boolean success;
    String message;
    T data;
}
