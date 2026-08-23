package com.example.COLLABORATION_SERVICE.utils;

import lombok.Getter;

import java.security.Principal;

@Getter
public class WebSocketUserPrincipal implements Principal {

    private final Long userId;
    private final String username;
    private final String role;

    public WebSocketUserPrincipal(
            Long userId,
            String username,
            String role
    ) {

        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    @Override
    public String getName() {

        return userId.toString();
    }
}
