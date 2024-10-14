package com.myproject.elearning.web.rest;

import com.myproject.elearning.service.AuthenticateService;
import com.myproject.elearning.service.dto.ApiResponse;
import com.myproject.elearning.service.dto.JwtAuthenticationResponse;
import com.myproject.elearning.service.dto.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authorize(@RequestBody LoginDTO loginDTO) {
        // Load username/password to Security
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsernameOrEmail(), loginDTO.getPassword());
        // This requires writing a loadUserByUsername method
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Load authentication information into SecurityContext (if authentication is successful)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = authenticateService.createToken(authentication);
        JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse(jwtToken);
        ApiResponse<JwtAuthenticationResponse> body = new ApiResponse<>();
        body.setSuccess(true);
        body.setMessage("Success");
        body.setData(authenticationResponse);

        return ResponseEntity.ok().body(body);
    }
}
