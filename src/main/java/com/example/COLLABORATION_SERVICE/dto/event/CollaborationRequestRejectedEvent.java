package com.example.COLLABORATION_SERVICE.dto.event;

import lombok.Builder;

@Builder
public record CollaborationRequestRejectedEvent(

        Long senderId,
        Long receiverId

) {
}