package com.myproject.elearning.security;

import com.myproject.elearning.dto.projection.UserAuth;
import com.myproject.elearning.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;

    // * No longer in use.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = userRepository
                .findAuthDTOByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
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
