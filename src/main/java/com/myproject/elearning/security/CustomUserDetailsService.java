package com.myproject.elearning.security;

import com.myproject.elearning.dto.auth.UserAuthDTO;
import com.myproject.elearning.exception.problemdetails.InvalidIdException;
import com.myproject.elearning.repository.UserRepository;
import com.myproject.elearning.service.cache.RedisAuthService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Load a user from the database.
 * Custom user authentication.
 */
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RedisAuthService redisAuthService;

    public CustomUserDetailsService(UserRepository userRepository, RedisAuthService redisAuthService) {
        this.userRepository = userRepository;
        this.redisAuthService = redisAuthService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthDTO userAuthDTO = redisAuthService.getCachedUser(username);

        if (userAuthDTO == null) {
            userAuthDTO = userRepository.findAuthDTOByEmail(username)
                    .orElseThrow(() -> new InvalidIdException("Email not found!"));
            redisAuthService.setCachedUser(username, userAuthDTO);
        }

        return createSpringSecurityUser(userAuthDTO);
    }

    public void clearUserCache(String username) {
        redisAuthService.invalidateUserCache(username);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(UserAuthDTO userAuthDTO) {
        List<GrantedAuthority> grantedAuthorities = userAuthDTO.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                userAuthDTO.getId().toString(),
                userAuthDTO.getPassword(),
                grantedAuthorities);
    }
}
