package com.example.COLLABORAION_SERVICE.dto.event;

import lombok.Builder;

@Builder
public record CollaborationRequestAcceptedEvent(

        Long senderId,
        Long receiverId

) {
}
