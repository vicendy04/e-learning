package com.myproject.elearning.web.rest;

import static com.myproject.elearning.web.rest.utils.ResponseUtil.wrapErrorResponse;
import static com.myproject.elearning.web.rest.utils.ResponseUtil.wrapSuccessResponse;

import com.myproject.elearning.service.AuthenticateService;
import com.myproject.elearning.service.dto.request.LoginRequest;
import com.myproject.elearning.service.dto.response.ApiResponse;
import com.myproject.elearning.service.dto.response.JwtAuthenticationResponse;
import java.security.Principal;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthenticateController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticateService authenticateService;

    public AuthenticateController(
            AuthenticationManagerBuilder authenticationManagerBuilder, AuthenticateService authenticateService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authenticateService = authenticateService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authorize(@RequestBody LoginRequest loginRequest) {
        // Load username/password to Security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        // This requires writing a loadUserByUsername method
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Load authentication information into SecurityContext (if authentication is successful)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = authenticateService.createToken(authentication);
        JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse(jwtToken);
        ApiResponse<JwtAuthenticationResponse> response = wrapSuccessResponse("Success", authenticationResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * REST request to check if the current user is authenticated.
     *
     * @param principal the authentication principal.
     * @return the login name if the user is authenticated.
     */
    @GetMapping(value = "/authenticate")
    public ResponseEntity<ApiResponse<String>> isAuthenticated(Principal principal) {
        ApiResponse<String> response;
        if (Objects.isNull(principal)) {
            response = wrapErrorResponse("Not authenticated", "GUEST");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            response = wrapSuccessResponse("Authenticated", principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
}
