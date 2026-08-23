package com.example.COLLABORATION_SERVICE.dto;

import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ResearcherProfileResponse(

        UserProfileResponse user,
        PagedResponse<ResearchPaperResponse> papers,
        boolean connected,
        boolean pendingRequest

) {
}
