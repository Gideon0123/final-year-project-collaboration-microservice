package com.example.COLLABORATION_SERVICE.service.impl;

import com.example.COLLABORATION_SERVICE.entity.Conversation;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import com.example.COLLABORATION_SERVICE.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;

    @Override
    public Conversation getOrCreateConversation(
            Long userOne,
            Long userTwo
    ) {
        if (userOne == null || userTwo == null) {
            throw new IllegalArgumentException(
                    "Conversation participants cannot be null"
            );
        }

        if (userOne.equals(userTwo)) {
            throw new IllegalArgumentException(
                    "A conversation requires two different users"
            );
        }

        Long first = Math.min(userOne, userTwo);
        Long second = Math.max(userOne, userTwo);

        return conversationRepository
                .findByParticipantOneIdAndParticipantTwoId(
                        first,
                        second
                )
                .orElseGet(() -> {
                    Conversation conversation =
                            Conversation.builder()
                                    .participantOneId(first)
                                    .participantTwoId(second)
                                    .lastMessageAt(LocalDateTime.now())
                                    .build();

                    return conversationRepository.save(conversation);
                });
    }
}