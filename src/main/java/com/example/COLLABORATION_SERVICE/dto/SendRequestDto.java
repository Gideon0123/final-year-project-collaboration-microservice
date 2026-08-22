package com.example.COLLABORATION_SERVICE.dto;

import lombok.Builder;

@Builder
public record SendRequestDto(

        Long receiverId,
        String message

) {
}