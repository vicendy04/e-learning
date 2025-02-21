package com.myproject.elearning.config;

import static com.myproject.elearning.security.SecurityUtils.CLAIM_KEY_AUTHORITIES;
import static org.springframework.security.config.Customizer.withDefaults;

import com.myproject.elearning.security.JwtAuthEntryPoint;
import com.myproject.elearning.security.JwtDeniedHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <a href="https://www.danvega.dev/blog/spring-security-jwt">...</a>
 * little out-date, but useful
 * <a href="https://stackoverflow.com/questions/76339307/spring-security-deprecated-issue">...</a>
 * fix 'jwt()' is deprecated and marked for removal
 * <a href="https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-authorization-extraction">...</a>
 * Extracting Authorities Manually {@link #jwtAuthenticationConverter()}
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    JwtDecoder jwtDecoder;

    private static final String[] PUBLIC_GET_ENDPOINTS = {
        "/api/v1/posts/{id}",
        "/api/v1/courses/{courseId}",
        "/api/v1/courses",
        "/api/v1/courses/{courseId}/chapters",
        "/api/v1/courses/{courseId}/chapters/expanded"
    };

    private static final String[] PUBLIC_POST_ENDPOINTS = {"/api/v1/users"};
    private static final String[] PUBLIC_URLS = {
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/api-docs/**",
        "/chat-test.html",
        "/post-like-test.html",
        "/homepage.html",
        "/course-search.html",
        "course-detail.html",
        "/api/v1/courses/chat/**",
        "/ws/**",
        "/api/v1/auth/**",
        "/api/v1/courses/search",
        "/actuator/**",
        "/api/v1/test-performance/**",
        "/api/v1/test/**",
        "/api/demo/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, JwtAuthEntryPoint jwtAuthEntryPoint, JwtDeniedHandler jwtDeniedHandler)
            throws Exception {
        return http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.requestMatchers(PUBLIC_URLS)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder))
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtDeniedHandler))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthEntryPoint))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName(CLAIM_KEY_AUTHORITIES);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
