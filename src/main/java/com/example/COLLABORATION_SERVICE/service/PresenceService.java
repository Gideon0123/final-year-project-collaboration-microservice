package com.example.COLLABORATION_SERVICE.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PresenceService {

    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();

    public void online(Long userId) {
        onlineUsers.add(userId);
    }

    public void offline(Long userId) {
        onlineUsers.remove(userId);
    }

    public boolean isOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    @EventListener
    public void handleConnect(
            SessionConnectedEvent event
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(
                event.getMessage()
        );

        Principal principal = accessor.getUser();

        if (principal == null) {
            return;
        }

        Long userId =  Long.parseLong(
                principal.getName()
        );

        online(userId);

        log.info(
                "User {} connected",
                userId
        );
    }

    @EventListener
    public void handleDisconnect(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(
                event.getMessage()
        );

        Principal principal = accessor.getUser();

        if (principal == null) {
            return;
        }

        Long userId = Long.parseLong(
                principal.getName()
        );

        offline(userId);

        log.info(
                "User {} disconnected",
                userId
        );
    }
}
