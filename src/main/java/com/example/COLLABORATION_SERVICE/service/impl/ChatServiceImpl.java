package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;
import com.example.COLLABORATION_SERVICE.dto.DeliveryReceiptResponse;
import com.example.COLLABORATION_SERVICE.dto.ReadReceiptResponse;
import com.example.COLLABORATION_SERVICE.entity.Conversation;
import com.example.COLLABORATION_SERVICE.entity.Message;
import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import com.example.COLLABORATION_SERVICE.exception.ResourceNotFoundException;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import com.example.COLLABORATION_SERVICE.repository.MessageRepository;
import com.example.COLLABORATION_SERVICE.service.ChatService;
import com.example.COLLABORATION_SERVICE.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    @Override
    public void sendMessage(
            ChatMessageRequest request
    ) {
        Conversation conversation = conversationService.getOrCreateConversation(
                request.getSenderId(),
                request.getReceiverId()
        );

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .type(request.getType())
                .status(MessageStatus.SENT)
                .build();

        messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        ChatMessageResponse response =
                ChatMessageResponse.builder()
                        .id(message.getId())
                        .conversationId(conversation.getId())
                        .senderId(message.getSenderId())
                        .receiverId(message.getReceiverId())
                        .content(message.getContent())
                        .type(message.getType())
                        .status(message.getStatus())
                        .createdAt(message.getCreatedAt())
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/chatroom/" +
                        conversation.getId(),
                response
        );
    }

    @Override
    public void markAsDelivered(Long messageId) {

        Message message = messageRepository.findById(messageId).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Message not found"
                ));

        message.setStatus(MessageStatus.DELIVERED);
        messageRepository.save(message);

        DeliveryReceiptResponse response =
                DeliveryReceiptResponse.builder()
                        .messageId(messageId)
                        .status(MessageStatus.DELIVERED)
                        .receiverId(message.getReceiverId())
                        .build();

        messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(),
                "/queue/delivered",
                response
        );
    }

    @Override
    public void markAsRead(Long messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Message not found"
                ));

        message.setStatus(MessageStatus.READ);
        messageRepository.save(message);

        ReadReceiptResponse response =
                ReadReceiptResponse.builder()
                        .messageId(messageId)
                        .status(MessageStatus.DELIVERED)
                        .readerId(message.getReceiverId())
                        .build();

        messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(),
                "/queue/read",
                response
        );
    }
}
