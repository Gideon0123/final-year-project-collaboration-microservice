package com.example.COLLABORATION_SERVICE.dto.event;

import lombok.Builder;

@Builder
public record CollaborationRequestAcceptedEvent(

        Long senderId,
        Long receiverId

) {
}
