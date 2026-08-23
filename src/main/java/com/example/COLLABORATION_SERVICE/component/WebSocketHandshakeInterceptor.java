package com.example.COLLABORATION_SERVICE.component;

import com.example.COLLABORATION_SERVICE.security.CollaborationJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor
        implements HandshakeInterceptor {

    private final CollaborationJwtService jwtService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        String token = extractAccessToken(request);

        if (token == null) {
            return false;
        }

        if (!jwtService.validateAccessToken(token)) {
            return false;
        }

        Long userId =
                jwtService.extractUserId(token);

        String username = jwtService.extractUsername(token);

        String role = jwtService.extractRole(token);

        attributes.put("userId", userId);
        attributes.put("username", username);
        attributes.put("role", role);

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

    private String extractAccessToken(
            ServerHttpRequest request
    ) {
        HttpHeaders headers = request.getHeaders();

        List<String> cookieHeaders = headers.get(HttpHeaders.COOKIE);

        if (cookieHeaders == null) {
            return null;
        }

        for (String cookieHeader : cookieHeaders) {

            String[] cookies =
                    cookieHeader.split(";");

            for (String cookie : cookies) {

                String trimmed =
                        cookie.trim();

                if (trimmed.startsWith("accessToken=")) {

                    return trimmed.substring(
                            "accessToken=".length()
                    );
                }
            }
        }

        return null;
    }
}