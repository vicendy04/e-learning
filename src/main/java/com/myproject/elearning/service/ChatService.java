package com.myproject.elearning.service;

import com.myproject.elearning.constant.AuthConstants;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    public boolean hasAccessToRoom(Principal principal, String roomId) {
        if (!isValidAuthentication(principal)) {
            return false;
        }

        Authentication authentication = (Authentication) principal;
        Collection<String> userAuthorities = extractAuthorities(authentication);

        return hasAdminAccess(userAuthorities) || hasUserOrInstructorAccess(userAuthorities);
    }

    private boolean isValidAuthentication(Principal principal) {
        return principal instanceof Authentication;
    }

    private Collection<String> extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private boolean hasAdminAccess(Collection<String> authorities) {
        return authorities.contains(AuthConstants.ADMIN);
    }

    private boolean hasUserOrInstructorAccess(Collection<String> authorities) {
        return authorities.contains(AuthConstants.USER) || authorities.contains(AuthConstants.INSTRUCTOR);
    }
}
