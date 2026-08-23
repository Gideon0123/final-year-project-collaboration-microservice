package com.example.COLLABORATION_SERVICE.dto;

import com.example.COLLABORATION_SERVICE.enums.MessageStatus;
import com.example.COLLABORATION_SERVICE.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType type;
    private MessageStatus status;
    private LocalDateTime createdAt;
}