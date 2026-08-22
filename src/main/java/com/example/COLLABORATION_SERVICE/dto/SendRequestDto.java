package com.example.COLLABORATION_SERVICE.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record SendRequestDto(

        @NotNull
        Long receiverId,

        @NotBlank
        String message

) {}