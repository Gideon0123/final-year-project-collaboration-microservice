package com.example.COLLABORATION_SERVICE.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConnectionResponse(

        Long id,
        Long userOneId,
        Long userTwoId,
        LocalDateTime connectedAt

) {
}