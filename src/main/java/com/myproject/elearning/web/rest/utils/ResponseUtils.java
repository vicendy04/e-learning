package com.myproject.elearning.web.rest.utils;

import com.myproject.elearning.dto.common.ApiRes;

/**
 * Util class for wrapping response.
 */
public final class ResponseUtils {
    private ResponseUtils() {}

    public static <T> ApiRes<T> successRes(String message, T data) {
        return ApiRes.<T>builder()
                .success(Boolean.TRUE)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiRes<T> errorRes(String message, T data) {
        return ApiRes.<T>builder()
                .success(Boolean.FALSE)
                .message(message)
                .data(data)
                .build();
    }
}
