package com.myproject.elearning.web.rest.utils;

import com.myproject.elearning.dto.common.ApiResponse;

/**
 * Util class for wrapping response.
 */
public final class ResponseUtils {
    private ResponseUtils() {}

    public static <T> ApiResponse<T> wrapSuccessResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .success(Boolean.TRUE)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> wrapErrorResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .success(Boolean.FALSE)
                .message(message)
                .data(data)
                .build();
    }
}
