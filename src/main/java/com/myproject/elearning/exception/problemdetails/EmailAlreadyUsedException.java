package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.config.ErrorConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EmailAlreadyUsedException extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public EmailAlreadyUsedException() {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), null);
    }

    public EmailAlreadyUsedException(Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Email is already in use!"), cause);
    }

    public EmailAlreadyUsedException(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public EmailAlreadyUsedException(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    public static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorConstants.EMAIL_ALREADY_USED_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
