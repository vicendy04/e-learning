package com.myproject.elearning.security;

import static com.myproject.elearning.rest.utils.ResponseUtils.errorRes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.elearning.dto.common.ApiRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper;
    BearerTokenAuthenticationEntryPoint bearerTokenAuthEntryPointDelegate;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        bearerTokenAuthEntryPointDelegate.commence(request, response, authException);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setDetail(authException.getMessage());
        ApiRes<ProblemDetail> apiRes = errorRes("Unauthorized access", problemDetail);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiRes);
    }
}
