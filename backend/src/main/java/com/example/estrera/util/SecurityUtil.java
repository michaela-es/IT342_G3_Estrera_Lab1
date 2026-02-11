package com.example.estrera.util;

import com.example.estrera.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new RuntimeException("Invalid principal type: " +
                (principal != null ? principal.getClass().getName() : "null"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUser_id();
    }

    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }
}