package com.myproject.elearning.exception;

import java.net.URI;

/**
 * Error constants.
 */
public final class ErrorConstants {
    public static final String PROBLEM_BASE_URL = "https://www.elearning.com/problem";
    public static final URI INVALID_ID_URI = URI.create(PROBLEM_BASE_URL + "/invalid-id");
    public static final URI EMAIL_ALREADY_USED_URI = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI TOKEN_ISSUE_URI = URI.create(PROBLEM_BASE_URL + "/token-issue");

    private ErrorConstants() {}
}
