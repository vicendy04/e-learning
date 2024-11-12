package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.exception.constants.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidIdException extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public InvalidIdException(Long id) {
        super(HTTP_STATUS, problemDetailFrom("Id " + id + " not found"), null);
    }

    public InvalidIdException(Long id, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Id " + id + " not found"), cause);
    }

    public InvalidIdException(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public InvalidIdException(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    public static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.INVALID_ID_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
