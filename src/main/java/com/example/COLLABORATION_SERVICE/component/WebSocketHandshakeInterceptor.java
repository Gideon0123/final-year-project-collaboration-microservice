package com.example.COLLABORATION_SERVICE.component;

import com.example.COLLABORATION_SERVICE.security.CollaborationJwtService;
import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final CollaborationJwtService jwtService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        Cookie[] cookies = httpRequest.getCookies();

        if (cookies == null) {
            return false;
        }

        String token = null;

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null || token.isBlank()) {
            return false;
        }

        if (!jwtService.validateAccessToken(token)) {
            return false;
        }

        Long userId = jwtService.extractUserId(token);
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        WebSocketUserPrincipal principal = new WebSocketUserPrincipal(
                userId,
                username,
                role
        );

        attributes.put("userId", userId);
        attributes.put("username", username);
        attributes.put("role", role);
        attributes.put("principal", principal);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // Nothing required here.
    }
}