package com.example.erv.utils;

import com.example.erv.model.User;
import com.example.erv.repository.user.UserRepository;
import com.example.erv.service.permission.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    @Autowired
    private UserRepository userRepository;

	public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) return null;
        return userRepository.findByUsername(username).orElse(null);
    }

    public static void checkAuthority(String authority) {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(element -> element.getAuthority().equals("super_user")))
            return;
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().noneMatch(element -> element.getAuthority().equals(authority)))
            throw new AccessDeniedException("User does not have the necessary authority.");
    }

    public static void checkAuthority(Permissions.EntityPermission permission) {
        checkAuthority(permission.getName());
    }

    public static boolean isSuperUser(User user) {
        return user.getRole().getPermissionList().stream().anyMatch(e -> e.getName().equals("super_user"));
    }
}
