package com.example.COLLABORATION_SERVICE.security;

import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtChannelInterceptor
        implements ChannelInterceptor {

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            authenticate(accessor);
        }

        return message;
    }

    private void authenticate(
            StompHeaderAccessor accessor
    ) {

        Map<String, Object> sessionAttributes =
                accessor.getSessionAttributes();

        if (sessionAttributes == null) {
            throw new BadCredentialsException(
                    "WebSocket session attributes missing"
            );
        }

        Long userId =
                (Long) sessionAttributes.get("userId");

        String username =
                (String) sessionAttributes.get("username");

        String role =
                (String) sessionAttributes.get("role");

        if (userId == null || username == null) {
            throw new BadCredentialsException(
                    "Authenticated WebSocket identity missing"
            );
        }

        WebSocketUserPrincipal principal =
                new WebSocketUserPrincipal(
                        userId,
                        username,
                        role
                );

        accessor.setUser(principal);
    }
}