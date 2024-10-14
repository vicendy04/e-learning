package com.myproject.elearning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.elearning.service.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of the {@link AuthenticationEntryPoint} interface
 * by wrapping the response in {@link ProblemDetail} format.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final BearerTokenAuthenticationEntryPoint bearerTokenAuthEntryPointDelegate =
            new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        bearerTokenAuthEntryPointDelegate.commence(request, response, authException);

        ApiResponse<ProblemDetail> apiResponse = new ApiResponse<>();
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setDetail(authException.getMessage());
        apiResponse.setSuccess(false);
        apiResponse.setMessage("Unauthorized access");
        apiResponse.setData(problemDetail);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
