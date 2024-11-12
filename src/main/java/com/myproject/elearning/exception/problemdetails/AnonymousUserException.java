package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.exception.constants.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class AnonymousUserException extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public AnonymousUserException() {
        super(HTTP_STATUS, problemDetailFrom("Anonymous user cannot perform this action"), null);
    }

    public AnonymousUserException(Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Anonymous user cannot perform this action"), cause);
    }

    public AnonymousUserException(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public AnonymousUserException(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    private static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.ANONYMOUS_USER_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
