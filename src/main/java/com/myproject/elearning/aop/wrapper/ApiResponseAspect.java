package com.myproject.elearning.aop.wrapper;

import com.myproject.elearning.dto.response.ApiResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiResponseAspect {

    @Around("@annotation(apiResponseWrapper)")
    public Object wrapWithApiResponse(ProceedingJoinPoint joinPoint, ApiResponseWrapper apiResponseWrapper)
            throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();

            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage(apiResponseWrapper.message());
            apiResponse.setData(body);

            return ResponseEntity.status(responseEntity.getStatusCode()).body(apiResponse);
        }

        return result;
    }
}
