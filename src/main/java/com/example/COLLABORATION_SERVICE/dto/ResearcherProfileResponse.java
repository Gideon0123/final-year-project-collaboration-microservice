package com.example.COLLABORATION_SERVICE.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ResearcherProfileResponse(

        UserProfileResponse user,
        List<ResearchPaperResponse> papers,
        boolean connected,
        boolean pendingRequest

) {
}
