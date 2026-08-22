package com.example.COLLABORATION_SERVICE.service;

import com.example.COLLABORATION_SERVICE.dto.CollaborationRequestResponse;
import com.example.COLLABORATION_SERVICE.dto.SendRequestDto;

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
}