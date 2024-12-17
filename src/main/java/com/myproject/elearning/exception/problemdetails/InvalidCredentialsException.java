package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.exception.constants.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidCredentialsException extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public InvalidCredentialsException() {
        super(HTTP_STATUS, problemDetailFrom("Invalid username or password"), null);
    }

    public InvalidCredentialsException(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    private static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.INVALID_CREDENTIALS_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
