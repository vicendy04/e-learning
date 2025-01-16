package com.myproject.elearning.exception.problemdetails;

import com.myproject.elearning.constant.ErrorUriConstants;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class InvalidDiscountEx extends ErrorResponseException {
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public InvalidDiscountEx() {
        super(HTTP_STATUS, problemDetailFrom("Invalid discount code"), null);
    }

    public InvalidDiscountEx(Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom("Invalid discount code"), cause);
    }

    public InvalidDiscountEx(String message) {
        super(HTTP_STATUS, problemDetailFrom(message), null);
    }

    public InvalidDiscountEx(String message, Throwable cause) {
        super(HTTP_STATUS, problemDetailFrom(message), cause);
    }

    private static ProblemDetail problemDetailFrom(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HTTP_STATUS);
        problemDetail.setType(ErrorUriConstants.INVALID_DISCOUNT_CODE);
        problemDetail.setTitle(message);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
