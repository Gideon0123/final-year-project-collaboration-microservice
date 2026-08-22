package com.example.COLLABORATION_SERVICE.dto.event;

import lombok.Builder;

@Builder
public record CollaborationRequestSentEvent(

        Long senderId,
        Long receiverId,
        String message

) {
}