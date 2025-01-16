package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.constant.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EmailUsedEx extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public EmailUsedEx() {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), null);
    }

    public EmailUsedEx(Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), cause);
    }

    public EmailUsedEx(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public EmailUsedEx(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    private static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.EMAIL_ALREADY_USED_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
