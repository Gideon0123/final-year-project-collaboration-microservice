package com.example.COLLABORATION_SERVICE.component;

import com.example.COLLABORATION_SERVICE.service.PresenceService;
import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleSessionConnected(
            SessionConnectedEvent event
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(
                event.getMessage()
        );

        Principal principal = accessor.getUser();

        if (!(principal instanceof WebSocketUserPrincipal user)) {
            return;
        }

        String sessionId = accessor.getSessionId();

        if (sessionId == null) {
            return;
        }

        presenceService.connect(
                user.getUserId(),
                user.getUsername(),
                sessionId
        );
    }

    @EventListener
    public void handleSessionDisconnect(
            SessionDisconnectEvent event
    ) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        Principal principal =
                accessor.getUser();

        if (!(principal instanceof WebSocketUserPrincipal user)) {
            return;
        }

        String sessionId = event.getSessionId();

        if (sessionId == null) {
            return;
        }

        presenceService.disconnect(
                user.getUserId(),
                user.getUsername(),
                sessionId
        );
    }
}