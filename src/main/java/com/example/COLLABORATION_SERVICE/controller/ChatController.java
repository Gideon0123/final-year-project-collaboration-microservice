package com.example.COLLABORATION_SERVICE.controller;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.DeliveryReceiptRequest;
import com.example.COLLABORATION_SERVICE.dto.ReadReceiptRequest;
import com.example.COLLABORATION_SERVICE.dto.TypingEvent;
import com.example.COLLABORATION_SERVICE.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(
            ChatMessageRequest request,
            Principal principal
    ) {
        chatService.sendMessage(
                request,
                principal
        );
    }

    @MessageMapping("/chat.delivered")
    public void delivered(
            DeliveryReceiptRequest request,
            Principal principal
    ) {

        chatService.markAsDelivered(
                request.getMessageId(),
                principal
        );
    }

    @MessageMapping("/chat.read")
    public void read(
            ReadReceiptRequest request,
            Principal principal
    ) {

        chatService.markAsRead(
                request.getMessageId(),
                principal
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(
            TypingEvent event,
            Principal principal
    ) {

        // We will correct this properly when we implement
        // the authenticated typing flow.
    }
}
