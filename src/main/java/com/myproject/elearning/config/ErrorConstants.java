package com.myproject.elearning.config;

import java.net.URI;

/**
 * Error constants.
 */
public final class ErrorConstants {
    public static final String PROBLEM_BASE_URL = "https://www.elearning.com/problem";
    public static final URI INVALID_ID_URI = URI.create(PROBLEM_BASE_URL + "/invalid-id");

    private ErrorConstants() {}
}
