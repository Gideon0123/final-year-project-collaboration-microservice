package com.example.COLLABORATION_SERVICE.component;

import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        StompHeaderAccessor accessor =  MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class
        );

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes == null) {

                throw new IllegalStateException(
                        "WebSocket session attributes are missing"
                );
            }

            Object principalObject = sessionAttributes.get("principal");

            if (!(principalObject instanceof WebSocketUserPrincipal principal)) {

                throw new IllegalStateException(
                        "WebSocket authentication failed"
                );
            }

            accessor.setUser(principal);
        }

        return message;
    }
}
