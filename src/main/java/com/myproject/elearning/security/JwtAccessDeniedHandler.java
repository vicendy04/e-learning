package com.myproject.elearning.security;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.wrapErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.elearning.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Not working.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final BearerTokenAccessDeniedHandler bearerTokenAccessDeniedHandlerDelegate;
    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(
            BearerTokenAccessDeniedHandler bearerTokenAccessDeniedHandlerDelegate, ObjectMapper objectMapper) {
        this.bearerTokenAccessDeniedHandlerDelegate = bearerTokenAccessDeniedHandlerDelegate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        bearerTokenAccessDeniedHandlerDelegate.handle(request, response, accessDeniedException);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail(accessDeniedException.getMessage());

        ApiResponse<ProblemDetail> apiResponse = wrapErrorResponse("Access denied to this resource", problemDetail);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
