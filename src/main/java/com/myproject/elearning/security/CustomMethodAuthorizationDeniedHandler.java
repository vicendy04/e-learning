package com.myproject.elearning.security;

import static com.myproject.elearning.web.rest.utils.ResponseUtils.errorRes;

import com.myproject.elearning.dto.common.ApiRes;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Unused.
 *
 * @see org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#fallback-values-authorization-denied">Spring Security Documentation</a>
 */
@Component
public class CustomMethodAuthorizationDeniedHandler implements MethodAuthorizationDeniedHandler {
    @Override
    public ResponseEntity<ApiRes<ProblemDetail>> handleDeniedInvocation(
            MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail("Access denied to this resource");
        problemDetail.setProperty("isGranted", authorizationResult.isGranted());

        ApiRes<ProblemDetail> accessDeniedToThisResource = errorRes("Access denied to this resource", problemDetail);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessDeniedToThisResource);
    }
}
