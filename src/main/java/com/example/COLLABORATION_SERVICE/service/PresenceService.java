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

    private static final String USER_SESSION_KEY_PREFIX = "collaboration:presence:user:";
    private static final String SESSION_KEY_SUFFIX = ":sessions";
    private final RedisTemplate<String, String> presenceRedisTemplate;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void connect(
            Long userId,
            String username,
            String sessionId
    ) {
        String sessionKey = buildSessionKey(userId);

        Long previousSessionCount = presenceRedisTemplate.opsForSet().size(sessionKey);
        presenceRedisTemplate.opsForSet().add(sessionKey, sessionId);

        if (previousSessionCount == null || previousSessionCount == 0) {
            broadcastPresence(userId, username, PresenceStatus.ONLINE);
        }
    }

    public void disconnect(
            Long userId,
            String username,
            String sessionId
    ) {
        String sessionKey = buildSessionKey(userId);
        presenceRedisTemplate.opsForSet().remove(sessionKey, sessionId);

        Long remainingSessions = presenceRedisTemplate.opsForSet().size(sessionKey);

        if (remainingSessions == null || remainingSessions == 0) {

            presenceRedisTemplate.delete(sessionKey);
            broadcastPresence(userId, username, PresenceStatus.OFFLINE);
        }
    }

    public boolean isOnline(
            Long userId
    ) {
        String sessionKey = buildSessionKey(userId);
        Long sessionCount = presenceRedisTemplate.opsForSet().size(sessionKey);

        return sessionCount != null && sessionCount > 0;
    }

    private String buildSessionKey(
            Long userId
    ) {
        return USER_SESSION_KEY_PREFIX + userId + SESSION_KEY_SUFFIX;
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

        List<Conversation> conversations = conversationRepository.
                findUserConversations(userId);

        for (Conversation conversation : conversations) {
            Long otherUser;
            if (conversation.getParticipantOneId().equals(userId)) {
                otherUser = conversation.getParticipantTwoId();
            } else {
                otherUser = conversation.getParticipantOneId();
            }

            messagingTemplate.convertAndSendToUser(
                    otherUser.toString(),
                    "/queue/presence",
                    event
            );
        }
    }
}