package com.myproject.elearning.security;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.redis.RedisAuthService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Load a user from the database.
 * Custom user authentication.
 * No longer in use.
 */
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RedisAuthService redisAuthService;

    public CustomUserDetailsService(UserRepository userRepository, RedisAuthService redisAuthService) {
        this.userRepository = userRepository;
        this.redisAuthService = redisAuthService;
    }

    // * No longer in use.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Object cachedUser = redisAuthService.getCachedUser(username);

        if (cachedUser instanceof UserAuth userAuth) {
            return createSpringSecurityUser(userAuth);
        }

        UserAuth userAuth = userRepository.findAuthDTOByEmail(username).orElseGet(() -> {
            redisAuthService.setEmptyCache(username);
            throw new UsernameNotFoundException("Email not found");
        });

        redisAuthService.setCachedUser(username, userAuth);
        return createSpringSecurityUser(userAuth);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(UserAuth userAuth) {
        List<GrantedAuthority> grantedAuthorities = userAuth.getRoleNames().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                userAuth.getId().toString(), userAuth.getPassword(), grantedAuthorities);
    }
}
