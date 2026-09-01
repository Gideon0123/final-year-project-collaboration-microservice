package com.example.COLLABORATION_SERVICE.component;

import com.example.COLLABORATION_SERVICE.service.ChatService;
import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketConnectionListener {

    private final ChatService chatService;

    @EventListener
    public void handleSessionConnected(
            SessionConnectedEvent event
    ) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();

        if (principal == null) {
            return;
        }

        if (!(principal instanceof WebSocketUserPrincipal userPrincipal)) {
            return;
        }

        Long userId = userPrincipal.getUserId();

        chatService.deliverOfflineMessages(userId);
    }
}