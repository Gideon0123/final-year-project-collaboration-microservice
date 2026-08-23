package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;

import java.security.Principal;

public interface ChatService {

    void sendMessage(ChatMessageRequest request, Principal principal);
    void markAsDelivered(Long messageId, Principal principal);
    void markAsRead(Long messageId, Principal principal);
}