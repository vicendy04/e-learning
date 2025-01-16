package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.constant.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidIdEx extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public InvalidIdEx(Long id) {
        super(HTTP_STATUS, problemDetailFrom("Id " + id + " not found"), null);
    }

    public InvalidIdEx(Long id, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Id " + id + " not found"), cause);
    }

    public InvalidIdEx(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public InvalidIdEx(String message, Throwable cause) {
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
