package com.example.COLLABORAION_SERVICE.dto.event;

import lombok.Builder;

@Builder
public record CollaborationRequestRejectedEvent(

        Long senderId,
        Long receiverId

) {
}