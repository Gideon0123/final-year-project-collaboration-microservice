package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.entity.Conversation;

public interface ConversationService {

    Conversation getOrCreateConversation(
            Long userOne,
            Long userTwo
    );
}
