package com.myproject.elearning.exception.constants;

import java.net.URI;

/**
 * ErrorUriConstants.
 */
public final class ErrorUriConstants {
    public static final String PROBLEM_BASE_URL = "https://www.elearning.com/problem";
    public static final URI INVALID_ID_URI = URI.create(PROBLEM_BASE_URL + "/invalid-id");
    public static final URI EMAIL_ALREADY_USED_URI = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI TOKEN_ISSUE_URI = URI.create(PROBLEM_BASE_URL + "/token-issue");
    public static final URI ANONYMOUS_USER_URI = URI.create(PROBLEM_BASE_URL + "/anonymous-user-issue");

    private ErrorUriConstants() {}
}