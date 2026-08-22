package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.*;
import com.example.COLLABORATION_SERVICE.enums.AccountStatus;
import com.example.COLLABORATION_SERVICE.enums.Role;
import com.example.COLLABORATION_SERVICE.payload.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface CollaborationService {

    CollaborationRequestResponse sendRequest(
            Long senderId,
            SendRequestDto dto
    );

    CollaborationRequestResponse acceptRequest(
            Long requestId,
            Long currentUserId
    );

    CollaborationRequestResponse rejectRequest(
            Long requestId,
            Long currentUserId
    );

    PagedResponse<CollaborationRequestResponse> getSentRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    PagedResponse<CollaborationRequestResponse> getReceivedRequests(
            Long userId,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    void cancelRequest(
            Long requestId,
            Long currentUserId
    );

    PagedResponse<ConnectionResponse> getConnections(
            Long userId,
            int page,
            int size
    );

    ApiResponse<PagedResponse<UserProfileResponse>> searchResearchers(
            String keyword,
            Long id,

            String firstName,
            String lastName,
            String username,
            String email,
            String phoneNo,

            Role role,
            AccountStatus status,

            Boolean emailVerified,
            Boolean accountNonLocked,

            LocalDateTime createdAfter,
            LocalDateTime createdBefore,

            int page,
            int size,
            String sortBy
    );

    ResearcherProfileResponse getResearcherProfile(
            Long currentUserId,
            Long researcherId
    );
}