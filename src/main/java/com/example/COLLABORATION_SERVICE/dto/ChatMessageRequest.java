package com.example.COLLABORATION_SERVICE.dto;

import com.example.COLLABORATION_SERVICE.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType type;
}