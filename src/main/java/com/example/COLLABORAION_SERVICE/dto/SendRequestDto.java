package com.example.COLLABORAION_SERVICE.dto;

import lombok.Builder;

@Builder
public record SendRequestDto(

        Long receiverId,
        String message

) {
}