package com.AmanecerTropical.config;

import com.AmanecerTropical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserService userService;

    public boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get the username (email) from the authentication principal
        String username = authentication.getName();
        if (username == null) {
            return false;
        }

        // Get the user by email and check if the ID matches
        return userService.getUserByEmail(username)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}
