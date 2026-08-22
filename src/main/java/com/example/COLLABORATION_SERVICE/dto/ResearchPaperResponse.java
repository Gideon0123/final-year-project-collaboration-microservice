package com.example.COLLABORATION_SERVICE.dto;

import com.example.COLLABORATION_SERVICE.enums.ResearchStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResearchPaperResponse(

        Long id,
        String title,
        String abstractText,
        String category,
        ResearchStatus status,
        LocalDateTime updatedAt

) {
}
