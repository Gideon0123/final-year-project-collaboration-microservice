package com.example.COLLABORAION_SERVICE.dto;

import com.example.COLLABORAION_SERVICE.enums.CollaborationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CollaborationRequestResponse(

        Long id,
        Long senderId,
        Long receiverId,
        String message,
        CollaborationStatus status,
        LocalDateTime createdAt

) {
}