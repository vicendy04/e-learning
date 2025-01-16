package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.constant.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class TokenEx extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public TokenEx() {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), null);
    }

    public TokenEx(Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), cause);
    }

    public TokenEx(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public TokenEx(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message, cause), cause);
    }

    public static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.TOKEN_ISSUE_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    public static ProblemDetail problemDetailFrom(String message, Throwable cause) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.TOKEN_ISSUE_URI);
        problemDetail.setTitle(message);
        problemDetail.setDetail(cause.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
