package com.example.COLLABORATION_SERVICE.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) throws Exception {

        log.info(
                "WebSocket connection established. Session ID: {}",
                session.getId()
        );
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) throws Exception {

        log.info(
                "WebSocket message received. Session ID: {}, Payload: {}",
                session.getId(),
                message.getPayload()
        );

        session.sendMessage(
                new TextMessage(
                        "Server received: " + message.getPayload()
                )
        );
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            org.springframework.web.socket.CloseStatus status
    ) throws Exception {

        log.info(
                "WebSocket connection closed. Session ID: {}, Status: {}",
                session.getId(),
                status
        );
    }
}