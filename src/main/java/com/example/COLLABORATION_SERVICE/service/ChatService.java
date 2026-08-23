package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;

public interface ChatService {

    void sendMessage(ChatMessageRequest request);
    void markAsDelivered(Long messageId);
    void markAsRead(Long messageId);
}