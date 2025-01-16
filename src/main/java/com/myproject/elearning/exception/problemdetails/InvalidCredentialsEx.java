package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.constant.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidCredentialsEx extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.UNAUTHORIZED;

    public InvalidCredentialsEx() {
        super(HTTP_STATUS, problemDetailFrom("Invalid email or user does not exist"), null);
    }

    public InvalidCredentialsEx(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public InvalidCredentialsEx(String message, Throwable cause) {
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
