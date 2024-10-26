package com.myproject.elearning.web.rest.utils;

import com.myproject.elearning.dto.response.ApiResponse;

/**
 * Util class for wrapping response.
 */
public final class ResponseUtils {
    private ResponseUtils() {}

    public static <T> ApiResponse<T> wrapSuccessResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(Boolean.TRUE);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> wrapErrorResponse(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
