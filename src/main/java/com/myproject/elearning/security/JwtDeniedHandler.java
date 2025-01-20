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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Not working.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtDeniedHandler implements AccessDeniedHandler {
    ObjectMapper objectMapper;
    BearerTokenAccessDeniedHandler bearerTokenAccessDeniedHandlerDelegate;

    @Override
    public void handle(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        bearerTokenAccessDeniedHandlerDelegate.handle(request, response, accessDeniedException);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail(accessDeniedException.getMessage());

        ApiRes<ProblemDetail> apiRes = errorRes("Access denied to this resource", problemDetail);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), apiRes);
    }
}
