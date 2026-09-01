package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.ChatMessageRequest;
import com.example.COLLABORATION_SERVICE.dto.ChatMessageResponse;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;

import java.security.Principal;
import java.util.List;

public interface ChatService {

    void sendMessage(ChatMessageRequest request, Principal principal);
    void markAsDelivered(Long messageId, Principal principal);
    void markAsRead(Long messageId, Principal principal);
    PagedResponse<ChatMessageResponse> getConversationMessages(
            Long conversationId,
            int page,
            int size,
            Principal principal
    );
    void deliverOfflineMessages(Long userId);
}