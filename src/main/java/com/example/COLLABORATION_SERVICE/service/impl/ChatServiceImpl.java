package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;
import com.example.COLLABORATION_SERVICE.entity.Conversation;
import com.example.COLLABORATION_SERVICE.entity.Message;
import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import com.example.COLLABORATION_SERVICE.exception.ResourceNotFoundException;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import com.example.COLLABORATION_SERVICE.repository.MessageRepository;
import com.example.COLLABORATION_SERVICE.service.ChatService;
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

    @Override
    public void sendMessage(
            ChatMessageRequest request
    ) {
        Conversation conversation = conversationRepository.findById(request.getConversationId()
        ).orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found"
                )
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
}
