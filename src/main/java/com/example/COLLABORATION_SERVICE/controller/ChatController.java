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

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(
            ChatMessageRequest request
    ) {
        chatService.sendMessage(request);
    }

    @MessageMapping("/chat.delivered")
    public void delivered(
            DeliveryReceiptRequest request
    ) {
        chatService.markAsDelivered(
                request.getMessageId()
        );
    }

    @MessageMapping("/chat.read")
    public void read(
            ReadReceiptRequest request
    ) {
        chatService.markAsRead(
                request.getMessageId()
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(
            TypingEvent event
    ) {
        messagingTemplate.convertAndSendToUser(
                event.getReceiverId().toString(),
                "/queue/typing",
                event
        );
    }
}
