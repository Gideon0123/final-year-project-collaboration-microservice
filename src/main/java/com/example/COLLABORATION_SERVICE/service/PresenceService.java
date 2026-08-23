package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.PresenceEvent;
import com.example.COLLABORATION_SERVICE.entity.Conversation;
import com.example.COLLABORATION_SERVICE.enums.PresenceStatus;
import com.example.COLLABORATION_SERVICE.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final RedisTemplate<String, String> presenceRedisTemplate;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String ONLINE_USERS_KEY = "collaboration:presence:online-users";

    public void online(
            Long userId,
            String username
    ) {
        presenceRedisTemplate.opsForSet()
                .add(ONLINE_USERS_KEY, userId.toString());

        broadcastPresence(
                userId,
                username,
                PresenceStatus.ONLINE
        );
    }

    public void offline(
            Long userId,
            String username
    ) {
        presenceRedisTemplate.opsForSet()
                .remove(ONLINE_USERS_KEY, userId.toString());

        broadcastPresence(
                userId,
                username,
                PresenceStatus.OFFLINE
        );
    }

    public void connect(
            Long userId,
            String username,
            String sessionId
    ){

    }

    public void disconnect(
            Long userId,
            String username,
            String sessionId
    ){

    }

    public boolean isOnline(
            Long userId
    ) {

        Boolean member = presenceRedisTemplate
                .opsForSet()
                .isMember(
                        ONLINE_USERS_KEY,
                        userId.toString()
                );

        return Boolean.TRUE.equals(member);
    }

    private void broadcastPresence(
            Long userId,
            String username,
            PresenceStatus status
    ) {
        PresenceEvent event = PresenceEvent.builder()
                .userId(userId)
                .username(username)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();

        List<Conversation> conversations = conversationRepository.findUserConversations(
                userId
        );

        for (Conversation conversation : conversations) {

            Long otherUser = conversation.getParticipantOneId().equals(userId)
                    ?
                    conversation.getParticipantTwoId()
                    :
                    conversation.getParticipantOneId();

            messagingTemplate.convertAndSendToUser(
                    otherUser.toString(),
                    "/queue/presence",
                    event
            );
        }
    }

}