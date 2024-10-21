package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.config.ErrorConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidIdException extends ErrorResponseException {
    public InvalidIdException(Long id) {
        super(HttpStatus.NOT_FOUND, problemDetailFrom("Id " + id + " not found"), null);
    }

    public InvalidIdException(Long id, Throwable cause) {
        super(HttpStatus.NOT_FOUND, problemDetailFrom("Id " + id + " not found"), cause);
    }

    public static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(ErrorConstants.INVALID_ID_URI);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
