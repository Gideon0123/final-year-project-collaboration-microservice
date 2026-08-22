package com.example.COLLABORAION_SERVICE.service;

import com.example.COLLABORAION_SERVICE.entity.CurrentUser;
import com.example.COLLABORAION_SERVICE.exception.AccessDeniedException;
import com.example.COLLABORAION_SERVICE.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public CurrentUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal()
                instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Not authenticated");
        }

        return CurrentUser.builder()
                .id(principal.getUserId())
                .email(principal.getEmail())
                .role(principal.getRole())
                .build();
    }

}