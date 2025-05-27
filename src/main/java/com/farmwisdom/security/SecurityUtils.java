package com.farmwisdom.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getId() : null;
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static boolean isCurrentUserAdmin() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null &&
                userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public static boolean isCurrentUserExpert() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null &&
                userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EXPERT"));
    }
}
