package com.myproject.elearning.service;

import com.myproject.elearning.constant.AuthoritiesConstants;
import java.security.Principal;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    public boolean hasAccessToRoom(Principal principal, String roomId) {
        if (principal == null) {
            return false;
        }

        if (!(principal instanceof Authentication authentication)) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Kiểm tra nếu user là ADMIN
        boolean isAdmin =
                authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(AuthoritiesConstants.ADMIN::equals);

        if (isAdmin) {
            return true;
        }

        // Kiểm tra nếu user là USER hoặc INSTRUCTOR
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> AuthoritiesConstants.USER.equals(authority)
                        || AuthoritiesConstants.INSTRUCTOR.equals(authority));
    }
}
