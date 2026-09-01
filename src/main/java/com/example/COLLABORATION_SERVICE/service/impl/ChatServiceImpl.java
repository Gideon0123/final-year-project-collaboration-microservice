package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;
import com.example.COLLABORATION_SERVICE.dto.DeliveryReceiptResponse;
import com.example.COLLABORATION_SERVICE.dto.ReadReceiptResponse;
import com.example.COLLABORATION_SERVICE.entity.Conversation;
import com.example.COLLABORATION_SERVICE.entity.Message;
import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import com.example.COLLABORATION_SERVICE.exception.AccessDeniedException;
import com.example.COLLABORATION_SERVICE.exception.ResourceNotFoundException;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import com.example.COLLABORATION_SERVICE.repository.MessageRepository;
import com.example.COLLABORATION_SERVICE.service.ChatService;
import com.example.COLLABORATION_SERVICE.service.ConversationService;
import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    private Long getAuthenticatedUserId(
            Principal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException(
                    "WebSocket user is not authenticated"
            );
        }

        if (!(principal instanceof WebSocketUserPrincipal)) {
            throw new AccessDeniedException(
                    "Invalid WebSocket principal"
            );
        }

        return ((WebSocketUserPrincipal) principal).getUserId();
    }

    @Override
    public void sendMessage(
            ChatMessageRequest request,
            Principal principal
    ) {
        Long senderId = getAuthenticatedUserId(principal);
        Long receiverId = request.getReceiverId();

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException(
                    "You cannot send a private message to yourself"
            );
        }

        Conversation conversation = conversationService.getOrCreateConversation(
                senderId,
                receiverId
        );

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(senderId)
                .receiverId(receiverId)
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

//        messagingTemplate.convertAndSend(
//                "/topic/chatroom/" + conversation.getId(),
//                response
//        );

        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                response
        );
    }

    @Override
    public void markAsDelivered(Long messageId, Principal principal) {

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
    public void markAsRead(Long messageId, Principal principal) {

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

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversationMessages(
            Long conversationId,
            Principal principal
    ) {

        Long userId = getAuthenticatedUserId(principal);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found"
                ));

        if (!conversation.getParticipantOneId().equals(userId)
                && !conversation.getParticipantTwoId().equals(userId)) {

            throw new AccessDeniedException(
                    "You are not a participant in this conversation"
            );
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(
                conversationId
        );

        return messages.stream()
                .map(message ->
                        ChatMessageResponse.builder()
                                .id(message.getId())
                                .conversationId(conversation.getId())
                                .senderId(message.getSenderId())
                                .receiverId(message.getReceiverId())
                                .content(message.getContent())
                                .type(message.getType())
                                .status(message.getStatus())
                                .createdAt(message.getCreatedAt())
                                .build()
                )
                .toList();
    }
}
