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
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import com.example.COLLABORATION_SERVICE.repository.MessageRepository;
import com.example.COLLABORATION_SERVICE.service.ChatService;
import com.example.COLLABORATION_SERVICE.service.ConversationService;
import com.example.COLLABORATION_SERVICE.utils.WebSocketUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

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
    public void markAsDelivered(
            Long messageId,
            Principal principal
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(principal);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Message not found"
                        )
                );

        if (!message.getReceiverId().equals(authenticatedUserId)) {

            throw new AccessDeniedException(
                    "Only the receiver can mark this message as delivered"
            );
        }

        /*
         * Prevent invalid status transitions.
         *
         * A message should normally move:
         *
         * SENT -> DELIVERED -> READ
         */
        if (message.getStatus() == MessageStatus.READ ||
                message.getStatus() == MessageStatus.DELIVERED) {
            return;
        }

//        if (message.getStatus() == MessageStatus.DELIVERED) {
//            return;
//        }

        message.setStatus(MessageStatus.DELIVERED);
        messageRepository.save(message);

        DeliveryReceiptResponse response =
                DeliveryReceiptResponse.builder()
                        .messageId(message.getId())
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
    public void markAsRead(
            Long messageId,
            Principal principal
    ) {
        Long authenticatedUserId = getAuthenticatedUserId(principal);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Message not found"
                        )
                );

        if (!message.getReceiverId().equals(authenticatedUserId)) {

            throw new AccessDeniedException(
                    "Only the receiver can mark this message as read"
            );
        }

        /*
         * If the message is already READ,
         * there is nothing left to do.
         */
        if (message.getStatus() == MessageStatus.READ) {
            return;
        }

        /*
         * A message should normally be DELIVERED
         * before it becomes READ.
         */
        if (message.getStatus() != MessageStatus.DELIVERED) {

            throw new IllegalStateException(
                    "Message must be delivered before it can be marked as read"
            );
        }

        message.setStatus(MessageStatus.READ);
        messageRepository.save(message);

        ReadReceiptResponse response =
                ReadReceiptResponse.builder()
                        .messageId(message.getId())
                        .status(MessageStatus.READ)
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
    public PagedResponse<ChatMessageResponse> getConversationMessages(
            Long conversationId,
            int page,
            int size,
            Principal principal
    ) {
        Long userId = getAuthenticatedUserId(principal);

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Conversation not found"
                        )
                );

        /*
         * Only participants can retrieve
         * messages belonging to the conversation.
         */
        if (!conversation.getParticipantOneId().equals(userId)
                && !conversation.getParticipantTwoId().equals(userId)) {

            throw new AccessDeniedException(
                    "You are not a participant in this conversation"
            );
        }

        /*
         * Prevent invalid pagination values.
         */
        if (page < 1) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 1"
            );
        }

        if (size < 1) {
            throw new IllegalArgumentException(
                    "Size must be greater than or equal to 1"
            );
        }

        /*
         * Protect the API from someone requesting
         * an unnecessarily huge page.
         */
        if (size > 100) {
            throw new IllegalArgumentException(
                    "Page size cannot exceed 100"
            );
        }

        Pageable pageable = PageRequest.of(
                page - 1, size, Sort.by(
                        Sort.Direction.ASC,
                        "createdAt"
                )
        );

        Page<Message> messagePage = messageRepository.findByConversationId(
                conversationId,
                pageable
        );

        Page<ChatMessageResponse> responsePage =
                messagePage.map(message ->
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
                );

        return new PagedResponse<>(responsePage);
    }
}
