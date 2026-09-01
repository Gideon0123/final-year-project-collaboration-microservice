package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;

import java.security.Principal;
import java.util.List;

public interface ChatService {

    void sendMessage(ChatMessageRequest request, Principal principal);
    void markAsDelivered(Long messageId, Principal principal);
    void markAsRead(Long messageId, Principal principal);
    List<ChatMessageResponse> getConversationMessages(Long conversationId, Principal principal);
}